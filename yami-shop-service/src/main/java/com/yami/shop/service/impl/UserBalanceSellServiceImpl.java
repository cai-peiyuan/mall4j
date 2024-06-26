

package com.yami.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.model.UserBalanceSell;
import com.yami.shop.dao.UserBalanceSellMapper;
import com.yami.shop.service.UserBalanceSellService;
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
public class UserBalanceSellServiceImpl extends ServiceImpl<UserBalanceSellMapper, UserBalanceSell> implements UserBalanceSellService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserBalanceSellMapper userBalanceSellMapper;

    /**
     * 获取所有储值卡
     *
     * @return
     */
    @Override
    public List<UserBalanceSell> listAll() {
        return userBalanceSellMapper.selectList(new LambdaQueryWrapper<>());
    }
}
