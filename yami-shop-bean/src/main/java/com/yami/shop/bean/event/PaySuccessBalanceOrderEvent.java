

package com.yami.shop.bean.event;

import com.yami.shop.bean.model.Order;
import com.yami.shop.bean.model.UserBalanceOrder;
import lombok.Data;

import java.util.List;

/**
 * 充值订单付款成功的事件
 * @author
 */
@Data
public class PaySuccessBalanceOrderEvent {

    private UserBalanceOrder userBalanceOrder;

    private String orderNumber;
}
