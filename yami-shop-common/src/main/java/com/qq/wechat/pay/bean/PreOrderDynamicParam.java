package com.qq.wechat.pay.bean;

import lombok.Data;

/**
 * 动态下单参数
 */
@Data
public class PreOrderDynamicParam {
    /**
     * 订单号（业务）
     */
    String outTradeNo;
    /**
     * 用户openId
     */
    String openId;
    /**
     * 订单描述
     */
    String description;
    /**
     * 订单总金额，单位为分
     */
    int total;

    public PreOrderDynamicParam(String outTradeNo, String openId, String description, int total) {
        this.outTradeNo = outTradeNo;
        this.openId = openId;
        this.description = description;
        this.total = total;
    }
}
