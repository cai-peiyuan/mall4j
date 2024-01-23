

package com.yami.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.shop.bean.model.UserBalance;
import com.yami.shop.bean.model.UserBalanceDetail;

import java.util.List;

/**
 * 用户余额明细
 *
 * @author c'p'y
 */
public interface UserBalanceDetailService extends IService<UserBalanceDetail> {
    /**
     * 根据用户id获取用户余额明细
     * 按照useTime倒序排列
     * @param userId
     * @return
     */
    List<UserBalanceDetail> getUserBalanceDetailByUserId(String userId);

}
