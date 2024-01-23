package com.qq.wechat.pay.service;

import com.qq.wechat.pay.bean.PreOrderDynamicParam;
import com.wechat.pay.java.service.payments.model.Transaction;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.SortedMap;

/**
 * 微信支付
 */
public interface IWxPayService {
    /**
     * JSAPI下单
     *
     * @return
     */
    SortedMap<String, String> jsApiOrder(PreOrderDynamicParam preOrderDynamicParam) throws Exception;



    /**
     * 支付回调
     *
     * @param request
     * @return
     * @throws IOException
     */
    ResponseEntity callback(HttpServletRequest request) throws IOException;

    /**
     * 查询订单，根据商户订单号
     *
     * @return
     */
    Transaction queryWxPayOrderOutTradeNo(String transNo);

    /**
     * 关闭订单
     *
     * @return
     */
    void closePay(String outTradeNo);
}
