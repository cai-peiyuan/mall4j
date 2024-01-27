package com.yami.shop.admin.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yami.shop.bean.event.OrderDeliveryEvent;
import com.yami.shop.bean.model.Order;
import com.yami.shop.bean.model.OrderSettlement;
import com.yami.shop.service.OrderService;
import com.yami.shop.service.OrderSettlementService;
import com.yami.shop.service.WxShipInfoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 支付订单后续逻辑
 *
 * @author cpy
 */
@Component
@AllArgsConstructor
@Slf4j
public class OrderDeliveryListener {


    private final WxShipInfoService wxShipInfoService;

    private final OrderService orderService;
    private final OrderSettlementService orderSettlementService;

    /**
     * 购物订单已发货事件
     */
    @EventListener(OrderDeliveryEvent.class)
    @Async
    public void orderDeliveryEventListener(OrderDeliveryEvent event) {

        Order order = event.getOrder();
        log.debug("监听到购物订单已发货的事件 订单编号 {}", order.getOrderNumber());

        Order newOrder = orderService.getOne(new LambdaQueryWrapper<Order>().eq(Order::getOrderNumber, order.getOrderNumber()));
        log.debug("查询到最新的购物订单信息 准备向腾讯发送发货信息  订单信息 {}", newOrder.getProdName());

        OrderSettlement settlement = orderSettlementService.getOne(new LambdaQueryWrapper<OrderSettlement>().eq(OrderSettlement::getOrderNumber, order.getOrderNumber()));
        log.debug("查询订单结算信息表 结算单编号为 {}", settlement.getPayNo());

        //目前都是商家自主配送 将运单号码等信息发送给腾讯平台
        boolean result = wxShipInfoService.uploadOrderShipInfo(newOrder, settlement);
        if (result) {
            Order orderUpdate = new Order();
            orderUpdate.setOrderId(order.getOrderId());
            orderUpdate.setShipTowx(1);
            boolean b = orderService.updateById(orderUpdate);
            log.debug("发送物流信息成功 更新商品订单发货状态 {}", b);
        }
    }

}
