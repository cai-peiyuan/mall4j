/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */

package com.yami.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.model.UserBalance;
import com.yami.shop.dao.UserBalanceMapper;
import com.yami.shop.service.UserBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 用户余额
 *
 * @author c'p'y
 */
@Service
public class UserBalanceServiceImpl extends ServiceImpl<UserBalanceMapper, UserBalance> implements UserBalanceService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserBalanceMapper userBalanceMapper;

    /**
     * 根据用户id获取用户余额信息
     *
     * @param userId
     * @return
     */
    @Override
    public UserBalance getUserBalanceByUserId(String userId) {
        return userBalanceMapper.selectById(userId);
    }
}
