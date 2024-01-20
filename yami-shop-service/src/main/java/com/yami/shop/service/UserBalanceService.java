/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */

package com.yami.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.shop.bean.model.UserBalance;

/**
 * 用户余额
 *
 * @author c'p'y
 */
public interface UserBalanceService extends IService<UserBalance> {
    /**
     * 根据用户id获取用户余额信息
     *
     * @param userId
     * @return
     */
    UserBalance getUserBalanceByUserId(String userId);

    /**
     * 根据用户id获取用户余额信息
     *
     * @param userId
     * @param userMobile
     * @return
     */
    UserBalance getUserBalance(String userId, String userMobile);
}
