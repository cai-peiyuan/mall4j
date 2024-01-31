

package com.yami.shop.service;

import com.qq.wechat.pay.config.WechatPaySign;
import com.wechat.pay.java.core.notification.Notification;
import com.wechat.pay.java.service.partnerpayments.jsapi.model.Transaction;
import com.yami.shop.bean.app.param.PayParam;
import com.yami.shop.bean.model.OrderRefund;
import com.yami.shop.bean.model.UserBalanceOrder;
import com.yami.shop.bean.model.WxPayRefund;
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
     * @param transaction
     * @return
     */
    String handleWxPayNotifyTransaction(Transaction transaction);

    /**
     * 创建提交商品订单 微信预支付订单 并且返回支付参数
     * @param userId
     * @param shopId
     * @param payParam
     * @return
     * @author peiyuan.cai
     * @date 2024/1/24 11:03 星期三
     */
    WechatPaySign createWeChatPrePayOrder(String userId, Long shopId, PayParam payParam);

    /**
     * 创建余额充值 微信预支付订单 并且返回支付参数
     *
     * @param userBalanceOrder
     * @return
     * @author peiyuan.cai
     * @date 2024/1/23 13:03 星期二
     */
    WechatPaySign createWeChatPrePayOrder(UserBalanceOrder userBalanceOrder);

    /**
     * 创建微信退款订单
     *
     * @param orderRefund
     * @return
     * @author peiyuan.cai
     */
    WxPayRefund createWeChatRefundOrder(OrderRefund orderRefund);
}
