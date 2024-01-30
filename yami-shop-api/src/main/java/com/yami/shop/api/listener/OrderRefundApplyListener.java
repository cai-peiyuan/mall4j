

package com.yami.shop.api.listener;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.StrUtil;
import com.yami.shop.bean.app.dto.ShopCartItemDiscountDto;
import com.yami.shop.bean.app.dto.ShopCartItemDto;
import com.yami.shop.bean.app.dto.ShopCartOrderDto;
import com.yami.shop.bean.app.dto.ShopCartOrderMergerDto;
import com.yami.shop.bean.enums.OrderStatus;
import com.yami.shop.bean.event.OrderRefundApplyEvent;
import com.yami.shop.bean.event.OrderSubmitEvent;
import com.yami.shop.bean.model.*;
import com.yami.shop.bean.order.SubmitOrderOrder;
import com.yami.shop.common.constants.Constant;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.util.Arith;
import com.yami.shop.dao.*;
import com.yami.shop.security.api.util.SecurityUtils;
import com.yami.shop.service.ProductService;
import com.yami.shop.service.SkuService;
import com.yami.shop.service.UserAddrOrderService;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 订单申请退款的事件监听
 *
 * @author cai
 */
@Component()
@AllArgsConstructor
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

    }

}
