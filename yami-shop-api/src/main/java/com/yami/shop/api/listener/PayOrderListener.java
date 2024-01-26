package com.yami.shop.api.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wechat.pay.java.service.partnerpayments.jsapi.model.Transaction;
import com.yami.shop.bean.event.PaySuccessBalanceOrderEvent;
import com.yami.shop.bean.event.PaySuccessOrderEvent;
import com.yami.shop.bean.model.Category;
import com.yami.shop.bean.model.UserBalanceOrder;
import com.yami.shop.bean.model.WxPayPrepay;
import com.yami.shop.bean.order.PayOrderOrder;
import com.yami.shop.common.util.Json;
import com.yami.shop.service.UserBalanceOrderService;
import com.yami.shop.service.WxPayPrepayService;
import com.yami.shop.service.WxShipInfoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 支付订单后续逻辑
 *
 * @author cpy
 */
@Component("defaultPayOrderListener")
@AllArgsConstructor
@Slf4j
public class PayOrderListener {


    private final WxShipInfoService wxShipInfoService;

    /**
     * 购物订单已支付事件
     */
    @EventListener(PaySuccessOrderEvent.class)
    @Order(PayOrderOrder.DEFAULT)
    @Async
    public void paySuccessOrderEventListener(PaySuccessOrderEvent event) {
        List<String> orderNumbers = event.getOrderNumbers();
        for (String orderNumber : orderNumbers) {
            log.debug("购物订单已支付 订单编号 {} ", orderNumber);
        }
    }

    /**
     * 充值订单已支付事件
     */
    @EventListener(PaySuccessBalanceOrderEvent.class)
    @Order(PayOrderOrder.DEFAULT)
    @Async
    public void paySuccessBalanceOrderEventListener(PaySuccessBalanceOrderEvent event) {
        String orderNumber = event.getOrderNumber();
        UserBalanceOrder userBalanceOrder = event.getUserBalanceOrder();

        wxShipInfoService.uploadBalanceOrderShip(orderNumber, userBalanceOrder);

    }

}
