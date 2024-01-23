

package com.yami.shop.service;

import com.wechat.pay.java.core.notification.Notification;
import com.wechat.pay.java.service.partnerpayments.jsapi.model.Transaction;
import com.yami.shop.bean.app.param.PayParam;
import com.yami.shop.bean.pay.PayInfoDto;

import java.util.List;

/**
 * @author lgh on 2018/09/15.
 */
public interface PayService {

    /**
     * 支付
     * @param userId
     * @param payParam
     * @return
     */
    PayInfoDto pay(String userId, PayParam payParam);

    /**
     * 支付成功
     * @param payNo
     * @param bizPayNo
     * @return
     */
    List<String> paySuccess(String payNo, String bizPayNo);

    /**
     * 支付结果
     * @return
     */
    String handleWxPayNotifyTransaction(Transaction transaction);
}
