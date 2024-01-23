

package com.yami.shop.bean.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 储值卡售卖表
 *
 * @author c'p'y
 */
@Data
@TableName("tz_user_balance_sell")
public class UserBalanceSell implements Serializable {
    private static final long serialVersionUID = 2090714647038636896L;
    /**
     * 单品ID
     */
    @TableId
    private Long id;

    /**
     * 到账额度
     */
    private Double storedValue;

    /**
     * 售价
     */
    private Double sellValue;

    /**
     * 已售
     */
    private Integer sellCnt;

    /**
     * 状态 状态1可用2已售完0不显示
     */
    private String status;

}
