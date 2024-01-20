/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */

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
     * 状态 状态1可用2已售完0不显示
     */
    private String status;

}
