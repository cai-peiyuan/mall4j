package com.yami.shop.api.controller;

import com.jfinal.kit.Ret;
import com.qq.wechat.pay.WeChatPayUtil;
import com.wechat.pay.java.core.exception.ValidationException;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.partnerpayments.jsapi.model.Transaction;
import com.wechat.pay.java.service.refund.model.Refund;
import com.yami.shop.bean.event.BalanceOrderPaySuccessEvent;
import com.yami.shop.bean.event.OrderDeliveryEvent;
import com.yami.shop.bean.model.Order;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.Json;
import com.yami.shop.service.OrderService;
import com.yami.shop.service.PayService;
import com.yami.shop.service.WxPayNotifyService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

/**
 * 在接口文档中隐藏这个接口
 * 不对外显示
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
    private final OrderService orderService;

    private final WxPayNotifyService wxPayNotifyService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @GetMapping("/publishBalanceOrderPaySuccessEvent/{orderNumber}")
    public ServerResponseEntity publishBalanceOrderPaySuccessEvent(@PathVariable(name = "orderNumber") String orderNumber) {
        // 充值订单支付成功通知事件 和监听此事件执行进一步的数据操作  如上传发货信息等
        BalanceOrderPaySuccessEvent balanceOrderPaySuccessEvent = new BalanceOrderPaySuccessEvent();
        balanceOrderPaySuccessEvent.setUserBalanceOrder(null);
        balanceOrderPaySuccessEvent.setOrderNumber(orderNumber);
        eventPublisher.publishEvent(balanceOrderPaySuccessEvent);
        return ServerResponseEntity.success();
    }
    @GetMapping("/publishOrderDeliveryEvent/{orderNumber}")
    public ServerResponseEntity publishOrderDeliveryEvent(@PathVariable(name = "orderNumber") String orderNumber) {
        // 充值订单支付成功通知事件 和监听此事件执行进一步的数据操作  如上传发货信息等
        Order order = orderService.getOrderByOrderNumber(orderNumber);
        eventPublisher.publishEvent(new OrderDeliveryEvent(order));
        return ServerResponseEntity.success();
    }

    /**
     * 微信支付退款回调接口
     * doc -> https://pay.weixin.qq.com/docs/merchant/apis/mini-program-payment/payment-notice.html
     *
     * @param request
     * @param response
     */
    @PostMapping("/wechat/refund")
    public void wechatRefund(HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean success = true;
        String message = "";
        String code = "SUCCESS";
        try {
            // 接口传入的json格式字符串
            String requestBody = WeChatPayUtil.getBodyUTF8(request);
            Map<String, String> requestHeaders = WeChatPayUtil.getRequestHeaders(request);

            log.debug("微信支付退款通知接口处理数据 {}", requestBody);
            log.debug("微信支付退款通知接口 RequestHeader {}", Json.toJsonString(requestHeaders));
            RequestParam requestParam = WeChatPayUtil.getRequestParam(request, requestBody);
            NotificationParser parser = new NotificationParser(WeChatPayUtil.notificationConfig);
            try {
                Refund refund = parser.parse(requestParam, Refund.class);
                log.debug("通过加密数据解析的通知内容 {}", refund);
                wxPayNotifyService.saveNotify("/wechat/refund", requestBody, requestHeaders, refund, "");
            } catch (ValidationException e) {
                log.error("验证微信支付退款通知接口数据出错 {}", e.getLocalizedMessage());
                success = false;
                message = "sign verification failed" + e.getLocalizedMessage();
            }
        } catch (Exception e) {
            code = "FAIL";
            success = false;
            message = e.getLocalizedMessage();
            log.error("处理微信支付退款通知接口数据出错 {}", e.getLocalizedMessage());
            e.printStackTrace();
        }

        /**
         * 接收成功： HTTP应答状态码需返回200或204，无需返回应答报文。
         * 接收失败： HTTP应答状态码需返回5XX或4XX，同时需返回应答报文，格式如下：
         */
        if (success) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(Ret.by("code", code).set("message", message).toJson());
        }
    }

    /**
     * 微信支付交易成功回调接口
     * doc -> https://pay.weixin.qq.com/docs/merchant/apis/mini-program-payment/payment-notice.html
     *
     * @param request
     * @param response
     */
    @PostMapping("/wechat/transaction")
    public void wechatTransaction(HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean success = true;
        String message = "";
        String code = "SUCCESS";
        try {
            // 接口传入的json格式字符串
            String requestBody = WeChatPayUtil.getBodyUTF8(request);
            Map<String, String> requestHeaders = WeChatPayUtil.getRequestHeaders(request);
            log.debug("微信支付交易通知接口处理数据 {}", requestBody);
            log.debug("微信支付交易通知接口 RequestHeader {}", Json.toJsonString(requestHeaders));

            RequestParam requestParam = WeChatPayUtil.getRequestParam(request, requestBody);
            NotificationParser parser = new NotificationParser(WeChatPayUtil.notificationConfig);
            try {
                Transaction transaction = parser.parse(requestParam, Transaction.class);
                //处理支付结果通知
                String handleNotifyResult = payService.handleWxPayNotifyTransaction(transaction);
                log.debug("通过加密数据解析的通知内容 {}", transaction);
                wxPayNotifyService.saveNotify("/wechat/transaction", requestBody, requestHeaders, transaction, handleNotifyResult);
            } catch (ValidationException e) {
                log.error("验证微信支付成功通知接口数据出错 {}", e.getLocalizedMessage());
                success = false;
                message = "sign verification failed" + e.getLocalizedMessage();
            }
        } catch (Exception e) {
            code = "FAIL";
            success = false;
            message = e.getLocalizedMessage();
            log.error("处理微信支付交易通知接口数据出错 {}", e.getLocalizedMessage());
            e.printStackTrace();
        }

        /**
         * 接收成功： HTTP应答状态码需返回200或204，无需返回应答报文。
         * 接收失败： HTTP应答状态码需返回5XX或4XX，同时需返回应答报文，格式如下：
         */
        if (success) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(Ret.by("code", code).set("message", message).toJson());
        }
    }
}
