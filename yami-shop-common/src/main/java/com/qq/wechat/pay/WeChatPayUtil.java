package com.qq.wechat.pay;

import com.google.gson.Gson;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.http.Constant;
import com.wechat.pay.java.core.notification.Notification;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.core.util.GsonUtil;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension;
import com.wechat.pay.java.service.refund.RefundService;
import com.yami.shop.common.util.Json;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author c'p'y
 */
@Slf4j
public class WeChatPayUtil {
    private static Gson gson = GsonUtil.getGson();

    /**
     * 1、用商户平台上设置的APIv3密钥【微信商户平台—>账户设置—>API安全—>设置APIv3密钥】，记为key；
     * 商户APIV3密钥
     */
    public static String apiV3Key = "";

    /**
     * 支付交易通知
     */
    public static String WXPAY_NOTIFY_URL_TRANSACTION = "";

    /**
     * 退款交易通知
     */
    public static String WXPAY_NOTIFY_URL_REFUND = "";

    public static String appId = "";


    public static String appSecret = "";

    /**
     * 微信支付商户号
     */
    public static String merchantId = "";
    /**
     * 商户API私钥路径
     */
    public static String privateKeyPath = "";
    /**
     * 商户API私钥
     */
    public static String privateKey = "";
    /**
     * 商户证书序列号
     */
    public static String merchantSerialNumber = "";

    /**
     *
     */
    public static final String SIGN_TYPE = "WECHATPAY2-SHA256-RSA2048";

    /**
     *
     */
    public static final String TRANSFORMATION = "AES/GCM/NoPadding";

    public static Config config;

    public static NotificationConfig notificationConfig;
    public static JsapiService jsApiService;
    public static JsapiServiceExtension jsapiServiceExtension;

    public static RefundService refundService;

    /**
     * 从request中获取原始的数据字符串
     *
     * @param request
     * @return
     */
    public static String getBodyUTF8(HttpServletRequest request) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    /**
     * 将 request中的头部信息getHeaderNames()取出存入map中
     *
     * @param request
     * @return
     * @author peiyuan.cai
     * @date 2024/1/23 16:09 星期二
     */
    public static Map<String, String> getRequestHeaders(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            // 排除Cookie字段
            if (key.equalsIgnoreCase("Cookie")) {
                continue;
            }
            String value = request.getHeader(key);
            map.put(key.toLowerCase(), value);
        }
        return map;
    }

    /**
     * 获取支付通知信息
     *
     * @param body
     * @return
     * @author peiyuan.cai@
     * @date 2024/1/23 16:18 星期二
     */
    public static Notification getNotificationSummary(String body) {
        return gson.fromJson(body, Notification.class);
    }


    /**
     * @param request
     * @param requestBody
     * @return
     * @author peiyuan.cai@
     * @date 2024/1/23 15:59 星期二
     */
    public static RequestParam getRequestParam(HttpServletRequest request, String requestBody) {
        String wechatPaySerial = request.getHeader(Constant.WECHAT_PAY_SERIAL);
        String wechatPayNonce = request.getHeader(Constant.WECHAT_PAY_NONCE);
        String wechatSignature = request.getHeader(Constant.WECHAT_PAY_SIGNATURE);
        String wechatTimestamp = request.getHeader(Constant.WECHAT_PAY_TIMESTAMP);
        return new RequestParam.Builder().serialNumber(wechatPaySerial).nonce(wechatPayNonce).signature(wechatSignature).timestamp(wechatTimestamp).body(requestBody).build();
    }


    /**
     * 设置微信支付配置信息
     *
     * @param entries
     * @author peiyuan.cai
     * @date 2024/1/23 14:02 星期二
     */
    public static void setValues(Map<Object, Object> entries) {
        merchantId = (String) entries.get("wxpay_mchid");
        apiV3Key = (String) entries.get("wxpay_apiv3_key");
        WXPAY_NOTIFY_URL_TRANSACTION = (String) entries.get("wxpay_notify_url_transaction");

        WXPAY_NOTIFY_URL_REFUND = (String) entries.get("wxpay_notify_url_refund");
        merchantSerialNumber = (String) entries.get("wxpay_merchantSerialNumber");
        appId = (String) entries.get("wxapp_appId");
        appSecret = (String) entries.get("wxapp_secret");
        privateKey = (String) entries.get("wxpay_private_key");
        log.debug("微信支付参数初始化完成");
    }

    /**
     * 初始化配置
     *
     * @author peiyuan.cai
     * @date 2024/1/23 14:11 星期二
     */
    public static void initConfig() {
        // 使用自动更新平台证书的RSA配置
        // 一个商户号只能初始化一个配置，否则会因为重复的下载任务报错
        config = new RSAAutoCertificateConfig.Builder().merchantId(merchantId).privateKey(privateKey).merchantSerialNumber(merchantSerialNumber).apiV3Key(apiV3Key).build();
        notificationConfig = new RSAAutoCertificateConfig.Builder().merchantId(merchantId).privateKey(privateKey).merchantSerialNumber(merchantSerialNumber).apiV3Key(apiV3Key).build();
        jsApiService = new JsapiService.Builder().config(config).build();
        jsapiServiceExtension = new JsapiServiceExtension.Builder().config(config).build();
        refundService = new RefundService.Builder().config(config).build();
        log.debug("微信支付服务初始化完成");
    }
}
