package com.yami.shop.bean.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 商家自配送运单路由日志
 *
 * @author c'p'y
 */
@Data
@TableName("tz_delivery_order_route")
public class DeliveryOrderRoute implements Serializable {
    /**
     * ID
     */
    @TableId
    private Long id;

    private String expressNumber;

    private String orderNumber;

    private String info;

    private Long addOrderId;

    private Date createTime;

}
