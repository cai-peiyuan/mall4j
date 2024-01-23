

package com.yami.shop.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wechat.pay.java.core.notification.Notification;
import com.wechat.pay.java.service.partnerpayments.jsapi.model.Transaction;
import com.yami.shop.bean.enums.PayType;
import com.yami.shop.bean.event.PaySuccessOrderEvent;
import com.yami.shop.bean.model.*;
import com.yami.shop.bean.app.param.PayParam;
import com.yami.shop.bean.pay.PayInfoDto;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.util.Arith;
import com.yami.shop.dao.OrderMapper;
import com.yami.shop.dao.OrderSettlementMapper;
import com.yami.shop.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.yami.shop.common.constants.Constant.ORDER_TYPE_BALANCE;

/**
 * 订单支付平台服务
 *
 * @author lgh on 2018/09/15.
 */
@Service
@Slf4j
public class PayServiceImpl implements PayService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderSettlementMapper orderSettlementMapper;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private WxPayPrepayService wxPayPrepayService;

    @Autowired
    private UserBalanceOrderService userBalanceOrderService;

    @Autowired
    private UserBalanceService userBalanceService;

    @Autowired
    private UserBalanceDetailService userBalanceDetailService;


    @Autowired
    private Snowflake snowflake;

    /**
     * 不同的订单号，同一个支付流水号
     * 支持多个订单合并付款
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayInfoDto pay(String userId, PayParam payParam) {
        // 不同的订单号的产品名称
        StringBuilder prodName = new StringBuilder();
        // 支付单号
        String payNo = String.valueOf(snowflake.nextId());
        String[] orderNumbers = payParam.getOrderNumbers().split(StrUtil.COMMA);
        // 修改订单信息
        for (String orderNumber : orderNumbers) {
            OrderSettlement orderSettlement = new OrderSettlement();
            orderSettlement.setPayNo(payNo);
            orderSettlement.setPayType(payParam.getPayType());
            orderSettlement.setUserId(userId);
            orderSettlement.setOrderNumber(orderNumber);
            orderSettlementMapper.updateByOrderNumberAndUserId(orderSettlement);

            Order order = orderMapper.getOrderByOrderNumber(orderNumber);
            prodName.append(order.getProdName()).append(StrUtil.COMMA);
        }
        // 除了ordernumber不一样，其他都一样
        List<OrderSettlement> settlements = orderSettlementMapper.getSettlementsByPayNo(payNo);
        // 应支付的总金额
        double payAmount = 0.0;
        for (OrderSettlement orderSettlement : settlements) {
            payAmount = Arith.add(payAmount, orderSettlement.getPayAmount());
        }

        prodName.substring(0, Math.min(100, prodName.length() - 1));

        PayInfoDto payInfoDto = new PayInfoDto();
        payInfoDto.setBody(prodName.toString());
        payInfoDto.setPayAmount(payAmount);
        payInfoDto.setPayNo(payNo);
        return payInfoDto;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<String> paySuccess(String payNo, String bizPayNo) {
        List<OrderSettlement> orderSettlements = orderSettlementMapper.selectList(new LambdaQueryWrapper<OrderSettlement>().eq(OrderSettlement::getPayNo, payNo));

        OrderSettlement settlement = orderSettlements.get(0);

        // 订单已支付
        if (settlement.getPayStatus() == 1) {
            throw new YamiShopBindException("订单已支付");
        }
        // 修改订单结算信息
        if (orderSettlementMapper.updateToPay(payNo, settlement.getVersion()) < 1) {
            throw new YamiShopBindException("结算信息已更改");
        }


        List<String> orderNumbers = orderSettlements.stream().map(OrderSettlement::getOrderNumber).collect(Collectors.toList());

        // 将订单改为已支付状态
        orderMapper.updateByToPaySuccess(orderNumbers, PayType.WECHATPAY.value());

        List<Order> orders = orderNumbers.stream().map(orderNumber -> orderMapper.getOrderByOrderNumber(orderNumber)).collect(Collectors.toList());
        eventPublisher.publishEvent(new PaySuccessOrderEvent(orders));
        return orderNumbers;
    }

    /**
     * 支付结果
     * 处理支付结果
     *
     * @param transaction
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String handleWxPayNotifyTransaction(Transaction transaction) {
        StringBuilder result = new StringBuilder();
        Date now = new Date();
        String attach = transaction.getAttach();
        if (StrUtil.equals(ORDER_TYPE_BALANCE, attach)) {
            String tradeType = transaction.getTradeType().name();
            //处理充值的用户支付通知订单逻辑
            String tradeState = transaction.getTradeState().name();
            String tradeStateDesc = transaction.getTradeStateDesc();
            String bankType = transaction.getBankType();
            if (transaction.getTradeState() == Transaction.TradeStateEnum.SUCCESS) {
                String outTradeNo = transaction.getOutTradeNo();
                /**
                 * 支付成功的通知
                 * 处理充值订单支付成功的逻辑
                 * 1、更新预支付订单数据
                 * 2、更新充值订单数据
                 * 3、更新用户充值余额值
                 * 4、添加用户余额变化明细
                 */

                // 1、更新预支付订单数据
                WxPayPrepay wxPayPrepay = wxPayPrepayService.getOne(new LambdaQueryWrapper<WxPayPrepay>().eq(WxPayPrepay::getOutTradeNo, outTradeNo).eq(WxPayPrepay::getAttach, attach));
                if (wxPayPrepay == null) {
                    log.warn("微信预支付订单不存在 attach = {} outTradeNo = {}", attach, outTradeNo);
                    result.append("微信预支付订单不存在 attach = " + attach + " outTradeNo = " + outTradeNo);
                } else {
                    if (StrUtil.equals(tradeState, wxPayPrepay.getTradeState())) {
                        result.append("微信预支付订单数据已经处理过 attach = " + attach + " outTradeNo = " + outTradeNo);
                    } else {
                        WxPayPrepay wxPayPrepayUpdate = new WxPayPrepay();

                        wxPayPrepayUpdate.setId(wxPayPrepay.getId());
                        wxPayPrepayUpdate.setTransactionId(transaction.getTransactionId());
                        wxPayPrepayUpdate.setTradeState(tradeState);
                        wxPayPrepayUpdate.setTradeStateDesc(tradeStateDesc);
                        wxPayPrepayUpdate.setBankType(bankType);
                        wxPayPrepayUpdate.setSuccessTime(transaction.getSuccessTime());
                        wxPayPrepayUpdate.setTradeStateDesc(tradeStateDesc);
                        wxPayPrepayUpdate.setUpdateTime(now);
                        boolean updateWxPayPrepay = wxPayPrepayService.updateById(wxPayPrepayUpdate);
                        result.append("更新微信预支付订单 ").append(" id = ").append(wxPayPrepay.getId()).append(" 更新结果 ").append(updateWxPayPrepay).append("\n");
                    }
                }

                // 2、更新充值订单数据
                UserBalanceOrder userBalanceOrder = userBalanceOrderService.getOne(new LambdaQueryWrapper<UserBalanceOrder>().eq(UserBalanceOrder::getOrderNumber, outTradeNo));
                if (userBalanceOrder == null) {
                    log.warn("充值订单数据不存在 orderNumber = {}", outTradeNo);
                    return "充值订单数据不存在 orderNumber = " + outTradeNo;
                } else {
                    if (userBalanceOrder.getIsPayed() == 1) {
                        log.warn("充值订单数据已经处理过 orderNumber = {}", outTradeNo);
                        result.append("充值订单数据已经处理过 orderNumber = " + outTradeNo);
                    } else {
                        UserBalanceOrder userBalanceOrderUpdate = new UserBalanceOrder();
                        userBalanceOrderUpdate.setOrderId(userBalanceOrder.getOrderId());
                        userBalanceOrderUpdate.setPayTime(now);
                        userBalanceOrderUpdate.setUpdateTime(now);
                        userBalanceOrderUpdate.setIsPayed(1);
                        userBalanceOrderUpdate.setStatus(3);
                        boolean updateUserBalanceOrder = userBalanceOrderService.updateById(userBalanceOrderUpdate);
                        result.append("更新充值订单 ").append(" orderId = ").append(userBalanceOrderUpdate.getOrderId()).append(" 更新结果 ").append(updateUserBalanceOrder).append("\n");


                        // 3、更新用户充值余额值
                        UserBalance userBalance = userBalanceService.getOne(new LambdaQueryWrapper<UserBalance>().eq(UserBalance::getUserId, userBalanceOrder.getUserId()));
                        double newBalance;
                        if (userBalance == null) {
                            log.warn("用户余额数据不存在 userId = {}", userBalanceOrder.getUserId());
                            return "用户余额数据不存在 userId = " + userBalanceOrder.getUserId();
                        } else {
                            UserBalance userBalanceUpdate = new UserBalance();
                            userBalanceUpdate.setUserId(userBalanceOrder.getUserId());
                            userBalanceUpdate.setUpdateTime(now);
                            newBalance = Arith.add(userBalance.getBalance(), userBalanceOrder.getTotal());
                            //设置最新余额
                            userBalanceUpdate.setBalance(newBalance);
                            boolean updateUserBalance = userBalanceService.updateById(userBalanceUpdate);
                            result.append("更新用户最新余额 ").append(" userId = ").append(userBalanceUpdate.getUserId()).append(" 更新结果 ").append(updateUserBalance).append("\n");
                        }


                        // 4、添加用户余额变化明细
                        UserBalanceDetail userBalanceDetail = new UserBalanceDetail();
                        userBalanceDetail.setUserId(userBalanceOrder.getUserId());
                        userBalanceDetail.setDetailType("1");
                        userBalanceDetail.setNewBalance(newBalance);
                        userBalanceDetail.setDescription("" + DateUtil.formatDateTime(now) + "充值" + userBalanceOrder.getTotal());
                        userBalanceDetail.setOrderNumber(userBalanceOrder.getOrderNumber());
                        userBalanceDetail.setUseTime(now);
                        userBalanceDetail.setUseTime(now);
                        // 消费是负数 充值是正数
                        userBalanceDetail.setUseBalance(userBalanceOrder.getTotal());

                        boolean saveUserBalanceDetail = userBalanceDetailService.save(userBalanceDetail);
                        result.append("添加用户余额变化明细 ").append(" 结果 ").append(saveUserBalanceDetail).append("\n");
                    }
                }




            }

        }
        return result.toString();
    }


}
