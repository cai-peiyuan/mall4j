

package com.yami.shop.bean.event;

import com.yami.shop.bean.model.UserBalanceOrder;
import lombok.Data;

/**
 * 充值订单付款成功的事件
 * @author
 */
@Data
public class BalanceOrderPaySuccessEvent {

    private UserBalanceOrder userBalanceOrder;

    private String orderNumber;
}
