package com.qq.wechat.pay;

import jakarta.servlet.http.HttpServletRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @author c'p'y
 */
public class WeChatPayUtil {

    /**
     * 1、用商户平台上设置的APIv3密钥【微信商户平台—>账户设置—>API安全—>设置APIv3密钥】，记为key；
     */
    public static String WXPAY_APIV3_KEY = "";

    public static String WXPAY_NOTIFY_URL = "";

    public static String WXAPP_APPID = "";

    public static String WXAPP_SECRET = "";

    /** 商户号 */
    public static String merchantId = "";
    /** 商户API私钥路径 */
    public static String privateKeyPath = "";
    /** 商户证书序列号 */
    public static String merchantSerialNumber = "";
    /** 商户APIV3密钥 */
    public static String apiV3Key = "";
    /**
     * 微信支付商户号
     */
    public static final String WXPAY_MCHID = "";
    /**
     *
     */
    public static final String SIGN_TYPE = "WECHATPAY2-SHA256-RSA2048";

    /**
     *
     */
    public static final String TRANSFORMATION = "AES/GCM/NoPadding";

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
}
