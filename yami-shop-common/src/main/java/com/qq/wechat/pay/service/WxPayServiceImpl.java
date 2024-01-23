package com.qq.wechat.pay.service;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.qq.wechat.pay.WeChatPayUtil;
import com.qq.wechat.pay.bean.PreOrderDynamicParam;
import com.qq.wechat.pay.config.WechatPaySign;
import com.qq.wechat.pay.config.WxV3PayConfig;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.exception.ValidationException;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.core.util.PemUtil;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import com.wechat.pay.java.service.payments.jsapi.model.*;
import com.wechat.pay.java.service.payments.model.Transaction;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Base64;
import java.util.SortedMap;
import java.util.TreeMap;


/**
 * @author lws
 * @since 2023-9-20
 */
@Slf4j
@Service
public class WxPayServiceImpl implements IWxPayService {



    /**
     * 获取支付签名
     *
     * @param prepayId
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    public static WechatPaySign sign(String prepayId) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        //随机字符串
        String nonceStr = RandomUtil.randomString(30);
        String packageStr = "prepay_id=" + prepayId;

        // 不能去除'.append("\n")'，否则失败
        String signStr = WxV3PayConfig.APP_ID + "\n" +
                timeStamp + "\n" +
                nonceStr + "\n" +
                packageStr + "\n";

        byte[] message = signStr.getBytes(StandardCharsets.UTF_8);

        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initSign(PemUtil.loadPrivateKeyFromString(WxV3PayConfig.PRIVATE_KEY));
        sign.update(message);
        String signStrBase64 = Base64.getEncoder().encodeToString(sign.sign());

        WechatPaySign wechatPaySign = new WechatPaySign();
        wechatPaySign.setPrepayId(prepayId);
        wechatPaySign.setTimeStamp(timeStamp);
        wechatPaySign.setNonceStr(nonceStr);
        wechatPaySign.setPackageStr(packageStr);
        wechatPaySign.setSign(signStrBase64);
        return wechatPaySign;
    }

    /**
     * 微信支付
     */
    @Override
    @Transactional
    public SortedMap<String, String> jsApiOrder(PreOrderDynamicParam preOrderDynamicParam) throws Exception {
        log.info("微信支付 >>>>>>>>>>>>>>>>> 金额：{}", preOrderDynamicParam.getTotal());
        PrepayRequest request = new PrepayRequest();
        SortedMap<String, String> params = new TreeMap<>();
        Amount amount = new Amount();
        amount.setTotal(preOrderDynamicParam.getTotal());
        Payer payer = new Payer();

        payer.setOpenid(preOrderDynamicParam.getOpenId());
        request.setAmount(amount);
        request.setPayer(payer);
        request.setAppid(WxV3PayConfig.APP_ID);
        request.setMchid(WxV3PayConfig.MCH_ID);
        request.setDescription(preOrderDynamicParam.getDescription());

        request.setNotifyUrl(WxV3PayConfig.PAY_BACK_URL);
        request.setOutTradeNo(preOrderDynamicParam.getOutTradeNo());

        PrepayResponse response = getJsapiService().prepay(request);


        WechatPaySign sign = sign(response.getPrepayId());
        // 订单号(业务需要）
        params.put("trans_no", preOrderDynamicParam.getOutTradeNo());
        params.put("appId", WxV3PayConfig.APP_ID);
        params.put("nonceStr", sign.getNonceStr());
        params.put("package", "prepay_id=" + sign.getPrepayId());
        params.put("signType", "RSA");
        params.put("timeStamp", sign.getTimeStamp());
        params.put("paySign", sign.getSign());

        return params;
    }


    /**
     * 微信回调
     *
     * @param request
     * @return
     * @throws IOException
     */
    @Transactional
    public ResponseEntity callback(HttpServletRequest request) throws IOException {
        log.info("微信回调v3 >>>>>>>>>>>>>>>>> ");
        RequestParam requestParam = new RequestParam.Builder()
                .serialNumber(request.getHeader("Wechatpay-Serial"))
                .nonce(request.getHeader("Wechatpay-Nonce"))
                .timestamp(request.getHeader("Wechatpay-Timestamp"))
                .signature(request.getHeader("Wechatpay-Signature"))
                .body(WeChatPayUtil.getBodyUTF8(request))
                .build();

        NotificationConfig config = new RSAAutoCertificateConfig.Builder()
                .merchantId(WxV3PayConfig.MCH_ID)
                .privateKey(WxV3PayConfig.PRIVATE_KEY)
                .merchantSerialNumber(WxV3PayConfig.MCH_SERIAL_NO)
                .apiV3Key(WxV3PayConfig.API_V3_KEY)
                .build();

        NotificationParser parser = new NotificationParser(config);

        try {
            // 验签、解密并转换成 Transaction（返回参数对象）
            Transaction transaction = parser.parse(requestParam, Transaction.class);
            log.info("微信支付回调 成功，解析" + JSON.toJSONString(transaction));
            // TODO 处理你的业务逻辑
            // 处理成功，返回 200 OK 状态码
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (ValidationException e) {
            log.error("sign verification failed", e);
            log.error("微信支付回调v3java失败=" + e.getMessage(), e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 关闭微信支付
     */
    @Override
    public void closePay(String outTradeNo) {
        CloseOrderRequest closeRequest = new CloseOrderRequest();
        closeRequest.setMchid(WxV3PayConfig.MCH_ID);
        closeRequest.setOutTradeNo(outTradeNo);
        getJsapiService().closeOrder(closeRequest);
    }

    @Override
    public Transaction queryWxPayOrderOutTradeNo(String transNo) {
        QueryOrderByOutTradeNoRequest request = new QueryOrderByOutTradeNoRequest();
        request.setMchid(WxV3PayConfig.MCH_ID);
        request.setOutTradeNo(transNo);
        Transaction transaction = getJsapiService().queryOrderByOutTradeNo(request);
        // TODO 处理你的业务逻辑
        return transaction;
    }

    /**
     * 创建小程序支付服务
     *
     * @return
     */
    protected JsapiService getJsapiService() {
        Config config =
                new RSAAutoCertificateConfig.Builder()
                        .merchantId(WxV3PayConfig.MCH_ID)
                        .privateKey(WxV3PayConfig.PRIVATE_KEY)
                        .merchantSerialNumber(WxV3PayConfig.MCH_SERIAL_NO)
                        .apiV3Key(WxV3PayConfig.API_V3_KEY)
                        .build();
        config.createSigner().getAlgorithm();
        return new JsapiService.Builder().config(config).build();
    }
}
