

package com.yami.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.shop.bean.model.UserBalanceSell;
import com.yami.shop.bean.model.UserBalanceStored;

import java.util.List;

/**
 * 储值卡
 *
 * @author c'p'y
 */
public interface UserBalanceStoredService extends IService<UserBalanceStored> {
    /**
     * 获取所有储值卡
     * @return
     */
    List<UserBalanceSell> listAll();

}
