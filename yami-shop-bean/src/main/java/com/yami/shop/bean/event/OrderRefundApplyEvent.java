

package com.yami.shop.bean.event;

import com.yami.shop.bean.model.Order;
import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * 订单申请退款事件
 * @author
 */
@Data
@AllArgsConstructor
public class OrderRefundApplyEvent {

    private Order order;
}
