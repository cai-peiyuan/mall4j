package com.qq.wechat.pay.config;

import lombok.Data;

/**
 * 微信支付签名对象
 */
@Data
public class WechatPaySign {
    /**
     * 预支付编号
     */
    private String prepayId;

    /**
     * 时间戳
     */
    private String timeStamp;

    /**
     * 随机字符串
     */
    private String nonceStr;

    /**
     * 小程序下单接口返回的prepay_id参数值，提交格式如：prepay_id=***
     */
    private String packageStr;

    /**
     * 支付签名
     */
    private String sign;
}
