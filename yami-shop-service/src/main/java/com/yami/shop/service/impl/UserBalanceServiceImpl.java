

package com.yami.shop.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.model.User;
import com.yami.shop.bean.model.UserBalance;
import com.yami.shop.dao.UserBalanceMapper;
import com.yami.shop.dao.UserMapper;
import com.yami.shop.service.UserBalanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户余额
 *
 * @author c'p'y
 */
@Service
@Slf4j
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
    @Transactional(rollbackFor = Exception.class)
    public UserBalance getUserBalanceByUserId(String userId) {
        UserBalance userBalance = userBalanceMapper.selectById(userId);
        if(userBalance == null){
            log.warn("用户余额数据不存在 userId = {}", userId);
            userBalance = getUserBalanceAddIfNotExists(userId, "");
        }

        /**
         * 如果余额表中没有手机号，则从用户表中更新手机号为会员卡号
         */
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
     * 如果不存在则新添加一个余额数据
     * @param userId
     * @param userMobile
     * @return
     */
    @Override
    public UserBalance getUserBalanceAddIfNotExists(String userId, String userMobile) {
        UserBalance userBalance = userBalanceMapper.selectById(userId);
        if (userBalance == null) {
            userBalance = new UserBalance();
            userBalance.setUserId(userId);
            userBalance.setBalance(Double.valueOf(0.00));
            userBalance.setCredits(0);
            userBalance.setCardNumber(userMobile);
            int insert = userBalanceMapper.insert(userBalance);
            log.warn("保存用户余额数据 userId = {}  结果 {}", userId, insert);
        }
        return userBalance;
    }
}
