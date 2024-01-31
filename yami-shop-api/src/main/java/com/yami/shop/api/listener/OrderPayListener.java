package com.yami.shop.api.listener;

import com.yami.shop.bean.event.BalanceOrderPaySuccessEvent;
import com.yami.shop.bean.event.OrderPaySuccessEvent;
import com.yami.shop.bean.model.Order;
import com.yami.shop.bean.model.UserBalanceOrder;
import com.yami.shop.common.util.Json;
import com.yami.shop.service.OrderService;
import com.yami.shop.service.WxShipInfoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.yami.shop.common.constants.Constant.KEY_SYS_CONFIG;

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

    private final OrderService orderService;

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 购物订单已支付事件
     */
    @EventListener(OrderPaySuccessEvent.class)
    public void paySuccessOrderEventListener(OrderPaySuccessEvent event) {
        List<String> orderNumbers = event.getOrderNumbers();
        log.debug("收到已支付的订单 {}", Json.toJsonString(orderNumbers));
        String print_order_auto = (String) redisTemplate.opsForHash().get(KEY_SYS_CONFIG, "print_order_auto");
        log.debug("配置是否自动打印订单小票 {}", print_order_auto);
        for (String orderNumber : orderNumbers) {
            log.debug("购物订单已支付 订单编号 {} ", orderNumber);
            Order orderByOrderNumber = orderService.getOrderByOrderNumber(orderNumber);
            String printOrder = orderService.printOrder(orderByOrderNumber);
            log.debug("购物订单编号 {} 打印结果返回 {}", orderNumber, printOrder);
        }
    }

    /**
     * 充值订单已支付事件
     */
    @EventListener(BalanceOrderPaySuccessEvent.class)
    public void paySuccessBalanceOrderEventListener(BalanceOrderPaySuccessEvent event) {
        String orderNumber = event.getOrderNumber();
        UserBalanceOrder userBalanceOrder = event.getUserBalanceOrder();
        log.debug("事件通知：充值订单支付成功，处理支付成功业务 orderNumber {} 充值订单  {}", orderNumber, Json.toJsonString(userBalanceOrder));
        wxShipInfoService.uploadBalanceOrderShip(orderNumber, userBalanceOrder);
    }

}
