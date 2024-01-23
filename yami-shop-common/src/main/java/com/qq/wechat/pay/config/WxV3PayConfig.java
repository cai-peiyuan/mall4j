package com.qq.wechat.pay.config;

import lombok.Data;

/**
 * 配置
 */
@Data
public class WxV3PayConfig {

    /**
     * 微信回调通知地址
     */
    public static String PAY_BACK_URL = "";

    //平台证书序列号
    public static String MCH_SERIAL_NO = "";

    //appID
    public static String APP_ID = "";

    //商户id
    public static String MCH_ID = "";

    // API V3密钥
    public static String API_V3_KEY = "";

    // 商户私钥 打开apiclient_key.pem拷贝出来的字符串
    public static String PRIVATE_KEY = "";

}
