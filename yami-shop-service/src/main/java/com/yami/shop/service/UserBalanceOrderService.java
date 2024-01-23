

package com.yami.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qq.wechat.pay.config.WechatPaySign;
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

    /**
     * 创建微信预支付订单 并且返回支付参数
     *
     * @param userBalanceOrder
     * @return
     * @author peiyuan.cai
     * @date 2024/1/23 13:03 星期二
     */
    WechatPaySign createWeChatPayPreOrder(UserBalanceOrder userBalanceOrder);
}
