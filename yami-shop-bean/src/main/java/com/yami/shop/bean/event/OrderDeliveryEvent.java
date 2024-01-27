package com.yami.shop.bean.event;

import com.yami.shop.bean.model.Order;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 订单发货事件
 *
 * @author
 */
@Data
@AllArgsConstructor
public class OrderDeliveryEvent {
    private Order order;
}
