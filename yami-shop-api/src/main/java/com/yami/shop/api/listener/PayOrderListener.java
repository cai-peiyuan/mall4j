

package com.yami.shop.api.listener;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.StrUtil;
import com.yami.shop.bean.app.dto.ShopCartItemDiscountDto;
import com.yami.shop.bean.app.dto.ShopCartItemDto;
import com.yami.shop.bean.app.dto.ShopCartOrderDto;
import com.yami.shop.bean.app.dto.ShopCartOrderMergerDto;
import com.yami.shop.bean.enums.OrderStatus;
import com.yami.shop.bean.event.PaySuccessOrderEvent;
import com.yami.shop.bean.event.SubmitOrderEvent;
import com.yami.shop.bean.model.*;
import com.yami.shop.bean.order.PayOrderOrder;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 支付订单后续逻辑
 *
 * @author cpy
 */
@Component("defaultPayOrderListener")
@AllArgsConstructor
@Slf4j
public class PayOrderListener {

    /**
     * 打印订单
     */
    @EventListener(PaySuccessOrderEvent.class)
    @Order(PayOrderOrder.DEFAULT)
    public void defaultSubmitOrderListener(PaySuccessOrderEvent event) {
        List<String> orderNumbers = event.getOrderNumbers();
        for (String orderNumber : orderNumbers) {
            log.debug("订单已支付 订单编号 {} " , orderNumber);
        }
    }

}
