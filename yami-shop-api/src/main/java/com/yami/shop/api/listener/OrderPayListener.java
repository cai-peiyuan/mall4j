package com.yami.shop.api.listener;

import com.yami.shop.bean.event.BalanceOrderPaySuccessEvent;
import com.yami.shop.bean.event.OrderPaySuccessEvent;
import com.yami.shop.bean.model.UserBalanceOrder;
import com.yami.shop.service.WxShipInfoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 支付订单后续逻辑
 *
 * @author cpy
 */
@Component
@AllArgsConstructor
@Slf4j
public class OrderPayListener {


    private final WxShipInfoService wxShipInfoService;

    /**
     * 购物订单已支付事件
     */
    @EventListener(OrderPaySuccessEvent.class)
    @Async
    public void paySuccessOrderEventListener(OrderPaySuccessEvent event) {
        List<String> orderNumbers = event.getOrderNumbers();
        for (String orderNumber : orderNumbers) {
            log.debug("购物订单已支付 订单编号 {} ", orderNumber);
        }
    }

    /**
     * 充值订单已支付事件
     */
    @EventListener(BalanceOrderPaySuccessEvent.class)
    @Async
    public void paySuccessBalanceOrderEventListener(BalanceOrderPaySuccessEvent event) {
        String orderNumber = event.getOrderNumber();
        UserBalanceOrder userBalanceOrder = event.getUserBalanceOrder();
        wxShipInfoService.uploadBalanceOrderShip(orderNumber, userBalanceOrder);
    }

}
