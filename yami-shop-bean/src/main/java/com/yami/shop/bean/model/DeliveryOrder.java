

package com.yami.shop.bean.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 商家自配送运单号
 * @author c'p'y
 */
@Data
@TableName("tz_delivery_order")
public class DeliveryOrder implements Serializable {
    /**
     * ID
     */
    @TableId
    private Long id;

    private String expressNumber;

    private String orderNumber;

    private Long addOrderId;

    private Integer expressStatus;

    private Date createTime;
}
