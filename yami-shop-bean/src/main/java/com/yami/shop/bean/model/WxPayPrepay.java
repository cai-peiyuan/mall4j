

package com.yami.shop.bean.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 微信预支付订单
 *
 * @author peiyuan.cai@
 * @date 2024/1/23 15:37 星期二
 */
@Data
@TableName("tz_wx_pay_prepay")
public class WxPayPrepay implements Serializable {
    /**
     * ID
     */
    @TableId
    private Long id;

    private String appId;

    private String mchId;

    private String description;

    private String outTradeNo;

    private String attach;

    private String notifyUrl;

    private String goodsTag;

    private Integer totalAmount;

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

    private String successTime;

    private String promotionDetail;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;


}
