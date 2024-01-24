

package com.yami.shop.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.model.User;
import com.yami.shop.bean.model.UserBalance;
import com.yami.shop.dao.UserBalanceMapper;
import com.yami.shop.dao.UserMapper;
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
    @Autowired
    private UserMapper userMapper;

    /**
     * 根据用户id获取用户余额信息
     *
     * @param userId
     * @return
     */
    @Override
    public UserBalance getUserBalanceByUserId(String userId) {
        UserBalance userBalance = userBalanceMapper.selectById(userId);
        if (StrUtil.isBlank(userBalance.getCardNumber())) {
            User user = userMapper.selectById(userId);
            String userMobile = user.getUserMobile();
            if (StrUtil.isNotBlank(userMobile)) {
                userBalance.setCardNumber(userMobile);
                userBalanceMapper.updateById(userBalance);
            }
        }
        return userBalance;
    }

    /**
     * 根据用户id获取用户余额信息
     *
     * @param userId
     * @param userMobile
     * @return
     */
    @Override
    public UserBalance getUserBalance(String userId, String userMobile) {
        UserBalance userBalance = userBalanceMapper.selectById(userId);
        if (userBalance == null) {
            userBalance = new UserBalance();
            userBalance.setUserId(userId);
            userBalance.setBalance(Double.valueOf(0.00));
            userBalance.setCredits(0);
            userBalance.setCardNumber(userMobile);
            userBalanceMapper.insert(userBalance);
        }
        return userBalance;
    }
}
