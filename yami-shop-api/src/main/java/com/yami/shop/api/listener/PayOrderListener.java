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

    private final WxPayPrepayService wxPayPrepayService;

    private final WxShipInfoService wxShipInfoService;

    private final UserBalanceOrderService userBalanceOrderService;

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
        if (orderNumber == null) {
            log.debug("orderNumber数据为空  ");
        }
        if (userBalanceOrder == null) {
            log.debug("userBalanceOrder数据为空  ");
            userBalanceOrder = userBalanceOrderService.getOne(new LambdaQueryWrapper<UserBalanceOrder>().eq(UserBalanceOrder::getOrderNumber, orderNumber));
        }
        log.debug("充值订单已支付 订单编号 {} ", orderNumber);
        WxPayPrepay wxPayPrepay = wxPayPrepayService.getOne(new LambdaQueryWrapper<WxPayPrepay>().eq(WxPayPrepay::getOutTradeNo, orderNumber).eq(WxPayPrepay::getTradeState, Transaction.TradeStateEnum.SUCCESS));
        if (wxPayPrepay == null) {
            log.debug("未查询到已支付的订单信息");
            return;
        }
        log.debug("查询到已支付的订单 {}", Json.toJsonString(wxPayPrepay));

        if (wxShipInfoService.checkTradeManaged()) {
            log.debug("查询小程序是否已开通发货信息管理服务");
            String s = wxShipInfoService.uploadBalanceOrderShipInfo(userBalanceOrder, wxPayPrepay);
            log.debug("准备向腾讯推送虚拟发货信息");
            log.debug("向腾讯推送虚拟发货信息请求接口返回 {}", s);

        } else {
            log.error("查询小程序是否已开通发货信息管理服务");
        }
    }

}
