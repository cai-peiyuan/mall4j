

package com.yami.shop.bean.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yami.shop.bean.app.dto.UserInfoDto;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author lanhai
 */
@Data
@TableName("tz_order")
public class Order implements Serializable {
    private static final long serialVersionUID = 6222259729062826852L;
    /**
     * 订单ID
     */
    @TableId
    private Long orderId;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 产品名称,多个产品将会以逗号隔开
     */
    private String prodName;

    /**
     * 订购用户ID
     */
    private String userId;

    /**
     * 订购流水号
     */
    private String orderNumber;

    /**
     * 总值
     */
    private Double total;

    /**
     * 实际总值
     */
    private Double actualTotal;

    /**
     * 支付方式 1 微信支付 2 支付宝
     */
    private Integer payType;

    /**
     * 订单备注
     */
    private String remarks;

    /**
     * 订单状态 -1 已取消 0:待付款 1:待发货 2:待收货 3:已完成
     */
    private Integer status;

    /**
     * 配送类型
     */

    private String dvyType;

    /**
     * 配送方式ID
     */

    private Long dvyId;

    /**
     * 物流单号
     */

    private String dvyFlowId;

    /**
     * 订单运费
     */

    private Double freightAmount;

    /**
     * 用户订单地址Id
     */
    private Long addrOrderId;

    /**
     * 订单商品总数
     */
    private Integer productNums;

    /**
     * 订单送达时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dvyArriveTime;


    /**
     * 订购时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 订单更新时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 付款时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date payTime;

    /**
     * 发货时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dvyTime;

    /**
     * 完成时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date finallyTime;

    /**
     * 取消时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date cancelTime;

    /**
     * 是否已经支付，1：已经支付过，0：，没有支付过
     */
    private Integer isPayed;

    /**
     * 用户订单删除状态，0：没有删除， 1：回收站， 2：永久删除
     */
    private Integer deleteStatus;

    /**
     * 0:默认,1:在处理,2:处理完成
     */
    private Integer refundSts;

    /**
     * 订单类型
     */
    private Integer orderType;

    /**
     * 订单关闭原因 1-超时未支付 2-退款关闭 4-买家取消 15-已通过货到付款交易
     */
    private Integer closeType;

    /**
     * 打印次数
     */
    private Integer printTimes;

    /**
     * 优惠总额
     */
    private Double reduceAmount;

    /**
     * 店铺名称
     */
    @TableField(exist = false)
    private String shopName;

    @TableField(exist = false)
    private List<OrderItem> orderItems;

    /**
     * 用户订单地址
     */
    @TableField(exist = false)
    private UserAddrOrder userAddrOrder;
    /**
     * 用户基本信息
     */
    @TableField(exist = false)
    private UserInfoDto userInfo;

    /**
     * 是否向腾讯发送发货标记
     */
    private Integer shipTowx;
}
