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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.model.OrderSettlement;
import com.yami.shop.bean.model.UserBalanceDetail;
import com.yami.shop.bean.model.UserBalanceSell;
import com.yami.shop.dao.UserBalanceDetailMapper;
import com.yami.shop.dao.UserBalanceSellMapper;
import com.yami.shop.service.UserBalanceDetailService;
import com.yami.shop.service.UserBalanceSellService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户余额明细
 *
 * @author c'p'y
 */
@Service
public class UserBalanceDetailServiceImpl extends ServiceImpl<UserBalanceDetailMapper, UserBalanceDetail> implements UserBalanceDetailService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserBalanceDetailMapper userBalanceDetailMapper;

    /**
     * 根据用户id获取用户余额明细
     * 按照useTime倒序排列
     *
     * @param userId
     * @return
     */
    @Override
    public List<UserBalanceDetail> getUserBalanceDetailByUserId(String userId) {
        return userBalanceDetailMapper.selectList(new LambdaQueryWrapper<UserBalanceDetail>().eq(UserBalanceDetail::getUserId, userId).orderByDesc(UserBalanceDetail::getUseTime));
    }
}