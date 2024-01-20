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

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户余额表
 * @author c'p'y
 */
@Data
@TableName("tz_user_balance")
public class UserBalance implements Serializable {
    private static final long serialVersionUID = 2090714647038636896L;
    /**
     * ID
     */
    @TableId(type = IdType.INPUT)
    private String userId;

    /**
     * 用户余额
     */
    private Double balance;

    /**
     * 更新时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 积分
     */
    private Integer credits;

    /**
     * cardNumber
     */
    private String cardNumber;

}
