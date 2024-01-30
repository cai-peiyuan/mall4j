package com.yami.shop.api.listener;

import cn.hutool.core.lang.Snowflake;
import com.yami.shop.bean.event.OrderRefundApplyEvent;
import com.yami.shop.bean.order.SubmitOrderOrder;
import com.yami.shop.dao.*;
import com.yami.shop.security.api.util.SecurityUtils;
import com.yami.shop.service.ProductService;
import com.yami.shop.service.SkuService;
import com.yami.shop.service.UserAddrOrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 订单申请退款的事件监听
 *
 * @author cai
 */
@Component()
@AllArgsConstructor
@Slf4j
public class OrderRefundApplyListener {

    private final UserAddrOrderService userAddrOrderService;

    private final ProductService productService;

    private final SkuService skuService;

    private final Snowflake snowflake;

    private final OrderItemMapper orderItemMapper;

    private final SkuMapper skuMapper;

    private final ProductMapper productMapper;

    private final OrderMapper orderMapper;

    private final OrderSettlementMapper orderSettlementMapper;

    private final BasketMapper basketMapper;

    /**
     * 订单申请退款了
     */
    @EventListener(OrderRefundApplyEvent.class)
    @Order(SubmitOrderOrder.DEFAULT)
    public void defaultSubmitOrderListener(OrderRefundApplyEvent event) {
        Date now = new Date();
        String userId = SecurityUtils.getUser().getUserId();
        com.yami.shop.bean.model.Order order = event.getOrder();
        log.debug("已支付订单提交退款申请");
    }

}
