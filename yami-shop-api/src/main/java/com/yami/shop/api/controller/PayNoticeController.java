/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */

package com.yami.shop.api.controller;

import com.jfinal.kit.Ret;
import com.qq.wechat.pay.WeChatPayUtil;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.exception.ValidationException;
import com.wechat.pay.java.core.http.Constant;
import com.wechat.pay.java.core.notification.Notification;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.core.util.GsonUtil;
import com.wechat.pay.java.service.partnerpayments.jsapi.model.Transaction;
import com.wechat.pay.java.service.refund.model.Refund;
import com.yami.shop.service.PayService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * .在接口文档中隐藏这个接口  不对外显示
 *
 * @author cpy
 */
@Hidden
@RestController
@RequestMapping("/notice/pay")
@AllArgsConstructor
@Slf4j
public class PayNoticeController {
    /**
     * 小程序支付
     */
    private final PayService payService;


    /**
     * 微信支付退款回调接口
     * doc -> https://pay.weixin.qq.com/docs/merchant/apis/mini-program-payment/payment-notice.html
     *
     * @param request
     * @param response
     */
    @RequestMapping("/wechat/refund")
    public void wechatRefund(HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean success = true;
        String message = "";
        String code = "SUCCESS";
        try {
            // 接口传入的json格式字符串
            String requestBody = WeChatPayUtil.getBodyUTF8(request);
            log.debug("微信支付退款通知接口处理数据 {}", requestBody);
            // 微信支付通知对象
            Notification notification = GsonUtil.getGson().fromJson(requestBody, Notification.class);
            /**
             * 获取resource.algorithm中描述的算法（目前为AEAD_AES_256_GCM），以及resource.nonce和resource.associated_data；
             */
            String algorithm = notification.getResource().getAlgorithm();
            String nonce = notification.getResource().getNonce();
            String associatedData = notification.getResource().getAssociatedData();
            /**
             * 使用key、nonce和associated_data，对数据密文resource.ciphertext进行解密，得到JSON形式的资源对象。
             */
            String ciphertext = notification.getResource().getCiphertext();
            /**
             * AEAD_AES_256_GCM算法的接口细节，请参考rfc5116 (opens new window)。微信支付使用的密钥key长度为32个字节，随机串nonce长度12个字节，associated_data长度小于16个字节并可能为空。
             * Java回调解密Json取值不带引号。
             */
            String wechatPaySerial = request.getHeader(Constant.WECHAT_PAY_SERIAL);
            String wechatPayNonce = request.getHeader(Constant.WECHAT_PAY_NONCE);
            String wechatSignature = request.getHeader(Constant.WECHAT_PAY_SIGNATURE);
            String wechatTimestamp = request.getHeader(Constant.WECHAT_PAY_TIMESTAMP);
            // 构造 RequestParam
            RequestParam requestParam = new RequestParam.Builder().serialNumber(wechatPaySerial).nonce(wechatPayNonce).signature(wechatSignature).timestamp(wechatTimestamp).body(requestBody).build();
            // 如果已经初始化了 RSAAutoCertificateConfig，可直接使用
            // 没有的话，则构造一个
            NotificationConfig config = new RSAAutoCertificateConfig.Builder().merchantId(WeChatPayUtil.WXPAY_MCHID).privateKeyFromPath(WeChatPayUtil.privateKeyPath).merchantSerialNumber(WeChatPayUtil.merchantSerialNumber).apiV3Key(WeChatPayUtil.WXPAY_APIV3_KEY).build();
            // 初始化 NotificationParser
            NotificationParser parser = new NotificationParser(config);
            try {
                // 以支付通知回调为例，验签、解密并转换成 Transaction
                Refund refund = parser.parse(requestParam, Refund.class);
                /**
                 * 这里是退款通知数据 通过这个数据处理逻辑
                 */
                log.debug("通过加密数据解析的通知内容 {}", refund);
            } catch (ValidationException e) {
                log.error("验证微信支付退款通知接口数据出错 {}", e.getLocalizedMessage());
                // 签名验证失败，返回 401 UNAUTHORIZED 状态码
                success = false;
                message = "sign verification failed" + e.getLocalizedMessage();
            }
            // 如果处理失败，应返回 4xx/5xx 的状态码，例如 500 INTERNAL_SERVER_ERROR
            // 处理成功，返回 200 OK 状态码
        } catch (Exception e) {
            code = "FAIL";
            success = false;
            message = e.getLocalizedMessage();
            log.error("处理微信支付退款通知接口数据出错 {}", e.getLocalizedMessage());
            e.printStackTrace();
        }

        if (success) {
            /**
             * 接收成功： HTTP应答状态码需返回200或204，无需返回应答报文。
             */
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            /**
             * 接收失败： HTTP应答状态码需返回5XX或4XX，同时需返回应答报文，格式如下：
             */
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(Ret.by("code", code).set("message", message).toJson());
        }
    /*  WxPayOrderNotifyResult parseOrderNotifyResult = wxMiniPayService.parseOrderNotifyResult(xmlData);
        String payNo = parseOrderNotifyResult.getOutTradeNo();
        String bizPayNo = parseOrderNotifyResult.getTransactionId();
        // 根据内部订单号更新order settlement
        payService.paySuccess(payNo, bizPayNo);

        return ServerResponseEntity.success();*/
    }

    /**
     * 微信支付交易成功回调接口
     * doc -> https://pay.weixin.qq.com/docs/merchant/apis/mini-program-payment/payment-notice.html
     *
     * @param request
     * @param response
     */
    @RequestMapping("/wechat/transaction")
    public void wechatTransaction(HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean success = true;
        String message = "";
        String code = "SUCCESS";
        try {
            // 接口传入的json格式字符串
            String requestBody = WeChatPayUtil.getBodyUTF8(request);
            log.debug("微信支付交易成功通知接口处理数据 {}", requestBody);
            // 微信支付通知对象
            Notification notification = GsonUtil.getGson().fromJson(requestBody, Notification.class);
            /**
             * 获取resource.algorithm中描述的算法（目前为AEAD_AES_256_GCM），以及resource.nonce和resource.associated_data；
             */
            String algorithm = notification.getResource().getAlgorithm();
            String nonce = notification.getResource().getNonce();
            String associatedData = notification.getResource().getAssociatedData();
            /**
             * 使用key、nonce和associated_data，对数据密文resource.ciphertext进行解密，得到JSON形式的资源对象。
             */
            String ciphertext = notification.getResource().getCiphertext();
            /**
             * AEAD_AES_256_GCM算法的接口细节，请参考rfc5116 (opens new window)。微信支付使用的密钥key长度为32个字节，随机串nonce长度12个字节，associated_data长度小于16个字节并可能为空。
             * Java回调解密Json取值不带引号。
             */
            String wechatPaySerial = request.getHeader(Constant.WECHAT_PAY_SERIAL);
            String wechatPayNonce = request.getHeader(Constant.WECHAT_PAY_NONCE);
            String wechatSignature = request.getHeader(Constant.WECHAT_PAY_SIGNATURE);
            String wechatTimestamp = request.getHeader(Constant.WECHAT_PAY_TIMESTAMP);
            // 构造 RequestParam
            RequestParam requestParam = new RequestParam.Builder().serialNumber(wechatPaySerial).nonce(wechatPayNonce).signature(wechatSignature).timestamp(wechatTimestamp).body(requestBody).build();
            // 如果已经初始化了 RSAAutoCertificateConfig，可直接使用
            // 没有的话，则构造一个
            NotificationConfig config = new RSAAutoCertificateConfig.Builder().merchantId(WeChatPayUtil.WXPAY_MCHID).privateKeyFromPath(WeChatPayUtil.privateKeyPath).merchantSerialNumber(WeChatPayUtil.merchantSerialNumber).apiV3Key(WeChatPayUtil.WXPAY_APIV3_KEY).build();
            // 初始化 NotificationParser
            NotificationParser parser = new NotificationParser(config);
            try {
                // 以支付通知回调为例，验签、解密并转换成 Transaction
                Transaction transaction = parser.parse(requestParam, Transaction.class);
                /**
                 * 这里是支付数据 通过这个数据处理逻辑
                 */
                log.debug("通过加密数据解析的通知内容 {}", transaction);
            } catch (ValidationException e) {
                log.error("验证微信支付交易成功通知接口数据出错 {}", e.getLocalizedMessage());
                // 签名验证失败，返回 401 UNAUTHORIZED 状态码
                success = false;
                message = "sign verification failed" + e.getLocalizedMessage();
            }
            // 如果处理失败，应返回 4xx/5xx 的状态码，例如 500 INTERNAL_SERVER_ERROR
            // 处理成功，返回 200 OK 状态码
        } catch (Exception e) {
            code = "FAIL";
            success = false;
            message = e.getLocalizedMessage();
            log.error("处理微信支付交易成功通知接口数据出错 {}", e.getLocalizedMessage());
            e.printStackTrace();
        }

        if (success) {
            /**
             * 接收成功： HTTP应答状态码需返回200或204，无需返回应答报文。
             */
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            /**
             * 接收失败： HTTP应答状态码需返回5XX或4XX，同时需返回应答报文，格式如下：
             */
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(Ret.by("code", code).set("message", message).toJson());
        }
    }
}
