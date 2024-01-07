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

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.app.param.BindPhoneParam;
import com.yami.shop.bean.enums.SmsType;
import com.yami.shop.bean.model.SmsLog;
import com.yami.shop.bean.model.User;
import com.yami.shop.bean.param.UserRegisterParam;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.util.RedisUtil;
import com.yami.shop.dao.SmsLogMapper;
import com.yami.shop.dao.UserMapper;
import com.yami.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Objects;

/**
 * @author lgh on 2018/09/11.
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SmsLogMapper smsLogMapper;

    @Override
    @Cacheable(cacheNames = "user", key = "#userId")
    public User getUserByUserId(String userId) {
        return userMapper.selectById(userId);
    }

    /**
     * 看看有没有校验验证码成功的标识
     * @param userRegisterParam
     * @param checkRegisterSmsFlag
     */
    @Override
    public void validate(UserRegisterParam userRegisterParam, String checkRegisterSmsFlag) {
        if (StrUtil.isBlank(userRegisterParam.getCheckRegisterSmsFlag())) {
            // 验证码已过期，请重新发送验证码校验
            throw new YamiShopBindException("验证码已过期，请重新发送验证码校验");
        } else {
            String checkRegisterSmsFlagMobile = RedisUtil.get(checkRegisterSmsFlag);
            if (!Objects.equals(checkRegisterSmsFlagMobile, userRegisterParam.getMobile())) {
                // 验证码已过期，请重新发送验证码校验
                throw new YamiShopBindException("验证码已过期，请重新发送验证码校验");
            }
        }
    }


    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public void bindUserPhoneNum(String userId, BindPhoneParam bindPhoneParam) {
        SmsLog smsLog = smsLogMapper.selectOne(new LambdaQueryWrapper<SmsLog>()
                .gt(SmsLog::getRecDate, DateUtil.beginOfDay(new Date()))
                .lt(SmsLog::getRecDate, DateUtil.endOfDay(new Date()))
                .eq(SmsLog::getUserId, userId)
                .eq(SmsLog::getType, SmsType.VALID.value())
                .eq(SmsLog::getUserPhone, bindPhoneParam.getMobile())
                .eq(SmsLog::getStatus, 1)
        );
        if (smsLog == null) {
            throw new YamiShopBindException("验证码已失效");
        }
        smsLog.setStatus(0);
        smsLogMapper.updateById(smsLog);
        User user = new User();
        user.setUserId(userId);
        user.setUserMobile(bindPhoneParam.getMobile());
        userMapper.updateById(user);
    }
}
