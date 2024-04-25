

package com.yami.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.shop.bean.model.UserBalanceOrder;

/**
 * 用户储值余额充值订单
 *
 * @author peiyuan.cai
 * @date 2024/1/23 12:56 星期二
 */
public interface UserBalanceOrderService extends IService<UserBalanceOrder> {
    /**
     * 创建充值预支付订单
     */
    UserBalanceOrder createBalanceOrder(String userId, Long shopId, Long cardId);

}
