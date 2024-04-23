

package com.yami.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.model.UserBalanceStored;
import com.yami.shop.dao.UserBalanceStoredMapper;
import com.yami.shop.service.UserBalanceStoredService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户余额
 *
 * @author c'p'y
 */
@Service
public class UserBalanceStoredServiceImpl extends ServiceImpl<UserBalanceStoredMapper, UserBalanceStored> implements UserBalanceStoredService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserBalanceStoredMapper userBalanceStoredMapper;

    /**
     * 获取所有储值卡
     *
     * @return
     */
    @Override
    public List<UserBalanceStored> listAll() {
        return userBalanceStoredMapper.selectList(new LambdaQueryWrapper<>());
    }
}
