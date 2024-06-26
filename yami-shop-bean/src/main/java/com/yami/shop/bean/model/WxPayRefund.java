

package com.yami.shop.bean.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 微信退款订单
 *
 * @author peiyuan.cai
 */
@Data
@TableName("tz_wx_pay_refund")
public class WxPayRefund implements Serializable {
    /**
     * ID
     */
    @TableId
    private Long id;

    private String appId;

    private String mchId;

    private String description;

    /**
     * 关联订单表中的订单编号多个使用逗号分割
     */
    private String orderNumbers;

    private String outTradeNo;

    private String outRefundNo;

    private String fundsAccount;

    private String attach;

    private String notifyUrl;

    private String goodsTag;

    private Long totalAmount;

    private Long refundAmount;

    private Long payerTotal;

    private Long payerRefund;

    private String fromAmount;
    private String goodsDetail;
    private String currency;

    private String payerOpenid;

    private String detail;

    private String sceneInfo;

    private String settleInfo;

    private String supportFapiao;

    private String prepayId;

    private String prepayTimestamp;

    private String prepayNonce;

    private String prepayPackage;

    private String prepaySignType;

    private String appPayResult;

    private String notifyResult;

    private String tradeType;

    private String orderType;

    private String tradeState;

    private String tradeStateDesc;

    private String transactionId;

    private String bankType;

    private String reason;

    private String promotionDetail;

    private String successTime;
    private String refundCreateTime;
    private String resultAmount;
    private String status;
    private String userReceivedAccount;
    private String channel;
    private String refundId;

    private String refundOrderType;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;


}
