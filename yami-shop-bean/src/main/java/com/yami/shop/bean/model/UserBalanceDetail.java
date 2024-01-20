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
 * 用户余额明细
 *
 * @author c'p'y
 */
@Data
@TableName("tz_user_balance_detail")
public class UserBalanceDetail implements Serializable {
    private static final long serialVersionUID = 2090714647038636896L;
    @TableId(type = IdType.INPUT)
    private String userId;
    private String detailType;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date useTime;
    private Double useBalance;
    private Double newBalance;
    private Integer useCredits;
    private Integer newCredits;
    private String orderNumber;
    private String description;
}
