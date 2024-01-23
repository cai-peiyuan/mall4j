

package com.yami.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qq.wechat.pay.WeChatPayUtil;
import com.wechat.pay.java.core.http.Constant;
import com.wechat.pay.java.core.notification.Notification;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.partnerpayments.jsapi.model.Transaction;
import com.wechat.pay.java.service.refund.model.Refund;
import com.yami.shop.bean.model.WxPayNotify;
import com.yami.shop.common.util.Json;
import com.yami.shop.dao.WxPayNotifyMapper;
import com.yami.shop.service.WxPayNotifyService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Slf4j
@Service
public class WxPayNotifyServiceImpl extends ServiceImpl<WxPayNotifyMapper, WxPayNotify> implements WxPayNotifyService {

    /**
     * 保存退款通知日志
     *
     * @param api
     * @param requestBody
     * @param requestHeaders
     * @param refund
     * @author peiyuan.cai@
     * @date 2024/1/23 16:22 星期二
     */
    @Override
    public void saveNotifyRefund(String api, String requestBody, Map<String, String> requestHeaders, Refund refund) {

    }

    /**
     * 保存交易通知日志
     *
     * @param api
     * @param requestBody
     * @param requestHeaders
     * @param transaction
     * @author peiyuan.cai@
     * @date 2024/1/23 16:23 星期二
     */
    @Override
    public void saveNotifyTransaction(String api, String requestBody, Map<String, String> requestHeaders, Transaction transaction) {

    }

    /**
     * 保存通知日志
     *
     * @param api
     * @param requestBody
     * @param requestHeaders
     * @param object
     * @author peiyuan.cai@
     * @date 2024/1/23 16:39 星期二
     */
    @Override
    public void saveNotify(String api, String requestBody, Map<String, String> requestHeaders, Object object, String handleNotifyResult) {
        try {
            WxPayNotify wxPayNotify = WxPayNotify.builder().build();
            Notification notification = WeChatPayUtil.getNotificationSummary(requestBody);
            wxPayNotify.setNotifyApi(api);
            wxPayNotify.setDataNotifyId(notification.getId());
            wxPayNotify.setCreateTime(new Date());
            wxPayNotify.setDataSummary(notification.getSummary());
            wxPayNotify.setDataResourceType(notification.getResourceType());
            wxPayNotify.setDataEventType(notification.getEventType());
            wxPayNotify.setReqBody(requestBody);
            wxPayNotify.setReqHeader(Json.toJsonString(requestHeaders));
            wxPayNotify.setDataResource(Json.toJsonString(notification.getResource()));
            wxPayNotify.setDataResourceDecrypt(Json.toJsonString(object));
            wxPayNotify.setNotifyHandleResult(handleNotifyResult);
            save(wxPayNotify);
        } catch (Exception e) {
            log.error("保存微信支付消息出错 " + e.getLocalizedMessage(), e);
        }
    }
}
