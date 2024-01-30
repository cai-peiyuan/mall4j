

package com.yami.shop.bean.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 配送人员
 * @author c'p'y
 */
@Data
@TableName("tz_refund_option")
public class RefundOption implements Serializable {
    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 顺序
     */
    private Integer seq;

    /**
     */
    private String refundName;

    /**
     * 状态 默认是1,0为下线
     */
    private Integer status;

}
