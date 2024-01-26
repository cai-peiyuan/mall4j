

package com.yami.shop.bean.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 上报给微信平台的物流信息
 * @author peiyuan.cai@
 */
@Data
@TableName("tz_wx_ship_info")
public class WxShipInfo implements Serializable {
    /**
     * ID
     */
    @TableId
    private Long id;

    private String appId;

    private String mchId;

    private Integer orderNumberType;

    private String transactionId;

    private String outTradeNo;

    private Integer logisticsType;

    private String deliveryMode;

    private String isAllDelivered;

    private String payerId;

    private String uploadTime;

    private String trackingNo;

    private String expressCompany;

    private String itemDesc;

    private String consignorContact;

    private String receiverContact;

    private String resultText;

    private String logisticsInfo;

    private String delivered;

    private String receipt;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}
