

package com.yami.shop.bean.event;

import com.yami.shop.bean.model.Order;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 *  购物订单付款成功的事件
 * @author
 */
@Data
public class PaySuccessOrderEvent {

    private List<Order> orders;

    private List<String> orderNumbers;
}
