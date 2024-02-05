

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
    UserBalance getUserBalanceAddIfNotExists(String userId, String userMobile);
}
