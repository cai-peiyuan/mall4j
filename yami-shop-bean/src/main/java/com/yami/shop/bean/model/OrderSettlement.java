

package com.yami.shop.bean.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author lanhai
 */
@Data
@TableName("tz_order_settlement")
public class OrderSettlement implements Serializable {
    /**
     * 支付结算单据ID
     */
    @TableId

    private Long settlementId;

    /**
     * 用户系统内部的订单号
     */
    private String payNo;

    /**
     * 外部订单流水号
     */
    private String bizPayNo;

    /**
     * 订单号
     */
    private String orderNumber;


    /**
     * 支付方式 0 余额支付 1 微信支付 2 支付宝
     */

    private Integer payType;

    /**
     * 支付方式 0 余额支付 1 微信支付 2 支付宝
     */
    private String payTypeName;

    /**
     * 支付金额
     */
    private Double payAmount;

    /**
     * 用户ID
     */

    private String userId;

    /**
     * 是否清算 0:否 1:是
     */

    private Integer isClearing;

    /**
     * 创建时间
     */

    private Date createTime;

    /**
     * 清算时间
     */

    private Date clearingTime;

    /**
     * 支付状态
     */
    private Integer payStatus;

    /**
     * 版本号
     */
    private Integer version;

}
