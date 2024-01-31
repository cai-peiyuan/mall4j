

package com.yami.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wechat.pay.java.core.notification.Notification;
import com.wechat.pay.java.service.partnerpayments.jsapi.model.Transaction;
import com.wechat.pay.java.service.refund.model.Refund;
import com.yami.shop.bean.model.WxPayNotify;

import java.util.Map;

public interface WxPayNotifyService extends IService<WxPayNotify> {

    /**
     * 保存退款通知日志
     * @param api
     * @param requestBody
     * @param requestHeaders
     * @param refund
     * @author peiyuan.cai@
     * @date 2024/1/23 16:22 星期二
     */
    void saveNotifyRefund(String api, String requestBody, Map<String, String> requestHeaders, Refund refund);

    /**
     * 保存交易通知日志
     * @param api
     * @param requestBody
     * @param requestHeaders
     * @param transaction
     * @author peiyuan.cai@
     * @date 2024/1/23 16:23 星期二
     */
    void saveNotifyTransaction(String api, String requestBody, Map<String, String> requestHeaders, Transaction transaction);

    /**
     * 保存通知日志
     * @param api
     * @param requestBody
     * @param requestHeaders
     * @param notification
     * @param parsedObject
     * @author peiyuan.cai@
     * @date 2024/1/23 16:39 星期二
     */
    void saveNotify(String api, String requestBody, Map<String, String> requestHeaders, Notification notification, Object parsedObject, String handleNotifyResult);
}
