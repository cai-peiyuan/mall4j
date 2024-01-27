package com.yami.shop.bean.event;

import com.yami.shop.bean.model.Order;
import org.springframework.context.ApplicationEvent;

/**
 * 取消订单的事件
 *
 * @author
 */
public class OrderCancelEvent extends ApplicationEvent {

    private Order order;


    public OrderCancelEvent(Order order) {
        super(order);
        this.order = order;
    }

    public Order getOrder() {
        return getOrder();
    }
}
