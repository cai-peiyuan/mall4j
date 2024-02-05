package com.yami.shop.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qq.wechat.pay.WeChatPayUtil;
import com.qq.wechat.pay.config.WechatPaySign;
import com.wechat.pay.java.core.notification.Notification;
import com.wechat.pay.java.service.partnerpayments.jsapi.model.Transaction;
import com.wechat.pay.java.service.payments.jsapi.model.Amount;
import com.wechat.pay.java.service.payments.jsapi.model.Payer;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import com.wechat.pay.java.service.refund.model.AmountReq;
import com.wechat.pay.java.service.refund.model.CreateRequest;
import com.wechat.pay.java.service.refund.model.Refund;
import com.wechat.pay.java.service.refund.model.Status;
import com.yami.shop.bean.app.param.PayParam;
import com.yami.shop.bean.enums.PayType;
import com.yami.shop.bean.event.BalanceOrderPaySuccessEvent;
import com.yami.shop.bean.event.OrderPaySuccessEvent;
import com.yami.shop.bean.model.*;
import com.yami.shop.bean.pay.PayInfoDto;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.util.Arith;
import com.yami.shop.common.util.Json;
import com.yami.shop.dao.OrderMapper;
import com.yami.shop.dao.OrderRefundMapper;
import com.yami.shop.dao.OrderSettlementMapper;
import com.yami.shop.dao.WxPayRefundMapper;
import com.yami.shop.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.yami.shop.common.constants.Constant.ORDER_TYPE_BALANCE;
import static com.yami.shop.common.constants.Constant.ORDER_TYPE_GOODS;

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
    private OrderRefundMapper orderRefundMapper;

    @Autowired
    private OrderSettlementMapper orderSettlementMapper;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private WxPayPrepayService wxPayPrepayService;

    @Autowired
    private WxPayRefundMapper wxPayRefundMapper;

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
    public PayInfoDto normalPay(String userId, PayParam payParam) {
        Date now = new Date();
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
            if (order.getIsPayed() == 1) {
                throw new YamiShopBindException("订单" + orderNumber + " 已支付 请勿重复支付" );
            }
            //商品名称拼接
            prodName.append(order.getProdName()).append(StrUtil.COMMA);
        }
        // 除了ordernumber不一样，其他都一样
        List<OrderSettlement> settlements = orderSettlementMapper.getSettlementsByPayNo(payNo);
        // 应支付的总金额
        double payAmount = 0.0;
        for (OrderSettlement orderSettlement : settlements) {
            payAmount = Arith.add(payAmount, orderSettlement.getPayAmount());
        }

        //商品名称拼接
        prodName.substring(0, Math.min(100, prodName.length() - 1));

        /**
         * 余额支付扣款
         */
        // 3、更新用户充值余额值
        UserBalance userBalance = userBalanceService.getUserBalanceByUserId(userId);
        if (userBalance.getBalance() < payAmount) {
            throw new YamiShopBindException("用户余额" + userBalance.getBalance() + " 不足以支付本订单" + payAmount);
        }

        UserBalance userBalanceUpdate = new UserBalance();
        userBalanceUpdate.setUserId(userId);
        userBalanceUpdate.setUpdateTime(now);
        //设置最新余额
        double newBalance = Arith.sub(userBalance.getBalance(), payAmount);
        userBalanceUpdate.setBalance(newBalance);

        boolean updateUserBalance = userBalanceService.updateById(userBalanceUpdate);

        if (!updateUserBalance) {
            throw new YamiShopBindException("用户余额扣款失败");
        }

        StringBuilder result = new StringBuilder();
        result.append("更新用户最新余额 ").append(" userId = ").append(userBalanceUpdate.getUserId()).append(" 最新余额 ").append(newBalance).append(" 更新结果 ").append(updateUserBalance).append("\n");
        log.debug(result.toString());

        // 4、添加用户余额变化明细
        UserBalanceDetail userBalanceDetail = new UserBalanceDetail();
        userBalanceDetail.setUserId(userId);
        userBalanceDetail.setDetailType("1");
        userBalanceDetail.setNewBalance(newBalance);
        userBalanceDetail.setDescription("在线消费 " + NumberUtil.decimalFormat("#.##", payAmount) + " 最新余额 " + newBalance);
        userBalanceDetail.setOrderNumber(payNo);
        userBalanceDetail.setUseTime(now);
        // 消费是负数 充值是正数
        userBalanceDetail.setUseBalance(Arith.sub(0, payAmount));

        boolean saveUserBalanceDetail = userBalanceDetailService.save(userBalanceDetail);
        if (!saveUserBalanceDetail) {
            throw new YamiShopBindException("用户余额明细记录失败");
        }
        result.append("添加用户余额变化明细 ").append(" 结果 ").append(saveUserBalanceDetail).append("\n");
        log.debug(result.toString());

        // 修改订单信息
        for (String orderNumber : orderNumbers) {
            OrderSettlement orderSettlement = new OrderSettlement();
            orderSettlement.setPayNo(payNo);
            orderSettlement.setBizPayNo(payNo);
            orderSettlement.setPayType(PayType.BALANCE.value());
            orderSettlement.setUserId(userId);
            orderSettlement.setOrderNumber(orderNumber);
            orderSettlement.setPayStatus(1);
            orderSettlement.setClearingTime(now);
            orderSettlement.setIsClearing(1);
            //更新订单结算信息
            int update = orderSettlementMapper.updateByOrderNumberAndUserId(orderSettlement);
            if (update < 1) {
                throw new YamiShopBindException("订单结算信息更新失败" + update);
            }
            result.append("更新订单结算信息 ").append(" orderNumber = ").append(orderNumber).append(" 更新结果 ").append(update).append("\n");
            // Order order = orderMapper.getOrderByOrderNumber(orderNumber);
        }

        // 将订单改为已支付状态
        int update = orderMapper.updateByToPaySuccess(Arrays.asList(orderNumbers), PayType.BALANCE.value());
        if (update < 1) {
            throw new YamiShopBindException("更新订单状态失败 " + update);
        }
        result.append("更新订单信息 ").append(" orderNumbers = ").append(Json.toJsonString(orderNumbers)).append(" 更新结果 ").append(update).append("\n");

        // 支付成功通知事件 和监听此事件执行进一步的数据操作  如打印小票、发送通知等
        // List<Order> orders = Arrays.asList(orderNumbers).stream().map(orderNumber -> orderMapper.getOrderByOrderNumber(orderNumber)).collect(Collectors.toList());
        OrderPaySuccessEvent orderPaySuccessEvent = new OrderPaySuccessEvent();
        orderPaySuccessEvent.setOrderNumbers(Arrays.asList(orderNumbers));
        eventPublisher.publishEvent(orderPaySuccessEvent);


        PayInfoDto payInfoDto = new PayInfoDto();
        payInfoDto.setBody(prodName.toString());
        payInfoDto.setPayAmount(payAmount);
        payInfoDto.setPayNo(payNo);
        payInfoDto.setSuccess(true);
        return payInfoDto;
    }


    /**
     * 原有系统逻辑 不适用
     *
     * @param payNo
     * @param bizPayNo
     * @return
     * @author peiyuan.cai@mapabc.com
     * @date 2024/1/26 16:07 星期五
     */
    @Deprecated
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

        // List<Order> orders = orderNumbers.stream().map(orderNumber -> orderMapper.getOrderByOrderNumber(orderNumber)).collect(Collectors.toList());
        OrderPaySuccessEvent orderPaySuccessEvent = new OrderPaySuccessEvent();
        orderPaySuccessEvent.setOrderNumbers(orderNumbers);
        eventPublisher.publishEvent(orderPaySuccessEvent);
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
        String result = null;
        String attach = transaction.getAttach();
        if (StrUtil.equals(ORDER_TYPE_BALANCE, attach)) {
            // 充值订单
            result = handleNotifyBalanceOrder(transaction, attach);
        } else if (StrUtil.equals(ORDER_TYPE_GOODS, attach)) {
            // 购物订单
            result = handleNotifyGoodsOrder(transaction, attach);
        }
        return result;
    }

    /**
     * 支付结果
     *
     * @param refund
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String handleWxPayNotifyRefund(Notification notification, Refund refund) {
        Date now = new Date();
        StringBuilder result = new StringBuilder();
        //判断如果是退款成功事件  继续处理
        if (StrUtil.equalsAnyIgnoreCase(notification.getEventType(), "REFUND.SUCCESS")) {
            String transactionId = refund.getTransactionId();
            String outTradeNo = refund.getOutTradeNo();
            log.debug("处理退款通知结果，查询是否为购物订单退款到账通知");
            OrderRefund orderRefund = orderRefundMapper.selectOne(new LambdaQueryWrapper<OrderRefund>()
                    //微信支付编号
                    .eq(transactionId != null, OrderRefund::getBizPayNo, transactionId)
                    //申请退款时 传入的内部退款订单编号
                    .eq(outTradeNo != null, OrderRefund::getOrderPayNo, outTradeNo));
            log.debug("购物订单退款查询结果 {}", Json.toJsonString(orderRefund));
            if (orderRefund != null) {
                OrderRefund update = new OrderRefund();
                // 主键
                update.setRefundId(orderRefund.getRefundId());

                update.setRefundSn(refund.getRefundId());
                update.setRefundSts(1);
                update.setRefundTime(new Date());
                int b = orderRefundMapper.updateById(update);
                result.append("更新购物退款订单信息 ").append(b).append(" transactionId = ").append(transactionId).append("\n");
            } else {
                result.append("未查询到购物退款订单信息 ").append(" transactionId = ").append(transactionId).append("\n");
            }

            log.debug("处理退款通知结果， 查询是否为购物订单退款到账通知");
            WxPayRefund wxPayRefund = wxPayRefundMapper.selectOne(new LambdaQueryWrapper<WxPayRefund>()
                    //微信支付编号
                    .eq(transactionId != null, WxPayRefund::getTransactionId, transactionId)
                    //申请退款时 传入的内部退款订单编号
                    .eq(outTradeNo != null, WxPayRefund::getOutTradeNo, outTradeNo));
            log.debug("微信支付平台退款订单查询结果 {}", Json.toJsonString(wxPayRefund));

            if (wxPayRefund != null) {
                WxPayRefund update = new WxPayRefund();
                // 主键
                update.setId(wxPayRefund.getId());
                update.setUserReceivedAccount(refund.getUserReceivedAccount());
                update.setSuccessTime(refund.getSuccessTime());
                com.wechat.pay.java.service.refund.model.Amount amount = refund.getAmount();
                update.setTotalAmount(amount.getTotal());
                update.setRefundAmount(amount.getRefund());
                update.setPayerTotal(amount.getPayerTotal());
                update.setPayerRefund(amount.getPayerRefund());
                update.setStatus(Status.SUCCESS.name());
                if (orderRefund != null) {
                    update.setOrderType(ORDER_TYPE_GOODS);
                } else {
                    update.setOrderType(ORDER_TYPE_BALANCE);
                }
                int b = wxPayRefundMapper.updateById(update);
                result.append("更新支付平台退款订单信息 ").append(b).append(" id = ").append(wxPayRefund.getId()).append("\n");
            } else {
                result.append("未查询到支付平台退款订单信息 ").append(" id = ").append(wxPayRefund.getId()).append("\n");
            }

            if (orderRefund != null) {
                Order order = orderMapper.getOrderByOrderNumber(orderRefund.getOrderNumber());
                if (order != null) {
                    Order update = new Order();
                    update.setOrderId(order.getOrderId());
                    update.setCloseType(2);
                    update.setRefundSts(2);
                    update.setFinallyTime(now);
                    update.setUpdateTime(now);
                    update.setStatus(6);
                    update.setRemarks("买家备注：" + order.getRemarks() + " 商家备注：订单退款已到账 退款交易单号" + orderRefund.getRefundId());
                    int updateOrder = orderMapper.updateById(update);
                    result.append("已更新购物订单信息 ").append(" orderNumber = ").append(orderRefund.getOrderNumber()).append(" 更新结果 ").append(updateOrder).append("\n");
                } else {
                    result.append("未查询到购物订单信息 ").append(" orderNumber = ").append(orderRefund.getOrderNumber()).append("\n");
                }
            }

        } else {
            log.debug("退款处理事件不正确 应该为  {}  实际为 {}", "REFUND.SUCCESS", notification.getEventType());
            result.append("退款处理事件不正确 ").append("\n");
        }
        return result.toString();
    }

    /**
     * 处理购物订单通知
     *
     * @param transaction
     * @param attach
     * @return
     * @author peiyuan.cai
     * @date 2024/1/24 14:38 星期三
     */
    private String handleNotifyGoodsOrder(Transaction transaction, String attach) {
        StringBuilder result = new StringBuilder();
        Date now = new Date();
        String tradeType = transaction.getTradeType().name();
        //处理购物订单逻辑
        String tradeState = transaction.getTradeState().name();
        String tradeStateDesc = transaction.getTradeStateDesc();
        String bankType = transaction.getBankType();
        if (transaction.getTradeState() == Transaction.TradeStateEnum.SUCCESS) {
            String outTradeNo = transaction.getOutTradeNo();
            /**
             * 支付成功的通知
             * 处理充值订单支付成功的逻辑
             * 1、更新预支付订单数据
             * 2、更新订单结算信息
             * 3、更新订单信息
             */
            // 1、更新预支付订单数据
            WxPayPrepay wxPayPrepay = wxPayPrepayService.getOne(new LambdaQueryWrapper<WxPayPrepay>().eq(WxPayPrepay::getOutTradeNo, outTradeNo).eq(WxPayPrepay::getAttach, attach));
            if (wxPayPrepay == null) {
                log.warn("微信预支付订单不存在 attach = {} outTradeNo = {}", attach, outTradeNo);
                result.append("微信预支付订单不存在 attach = " + attach + " outTradeNo = " + outTradeNo);
                //合并支付的订单数据 支付的内部订单编号存在预支付订单orderNumbers字段中 所以如果 没有预支付订单 无法进一步处理数据
                return result.toString();
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

                    String[] orderNumbers = wxPayPrepay.getOrderNumbers().split(StrUtil.COMMA);
                    String transactionId = transaction.getTransactionId();
                    // 修改订单信息
                    for (String orderNumber : orderNumbers) {
                        OrderSettlement orderSettlement = new OrderSettlement();
                        orderSettlement.setPayNo(outTradeNo);
                        orderSettlement.setBizPayNo(transactionId);
                        orderSettlement.setPayType(PayType.WECHATPAY.value());
                        orderSettlement.setUserId(wxPayPrepay.getPayerOpenid());
                        orderSettlement.setOrderNumber(orderNumber);
                        orderSettlement.setPayStatus(1);
                        orderSettlement.setClearingTime(now);
                        orderSettlement.setIsClearing(1);
                        //更新订单结算信息
                        int update = orderSettlementMapper.updateByOrderNumberAndUserId(orderSettlement);
                        result.append("更新订单结算信息 ").append(" orderNumber = ").append(orderNumber).append(" 更新结果 ").append(update).append("\n");
                        // Order order = orderMapper.getOrderByOrderNumber(orderNumber);
                    }

                    // 将订单改为已支付状态
                    int update = orderMapper.updateByToPaySuccess(Arrays.asList(orderNumbers), PayType.WECHATPAY.value());
                    result.append("更新订单信息 ").append(" orderNumbers = ").append(Json.toJsonString(orderNumbers)).append(" 更新结果 ").append(update).append("\n");

                    // 支付成功通知事件 和监听此事件执行进一步的数据操作  如打印小票、发送通知等
                    // List<Order> orders = Arrays.asList(orderNumbers).stream().map(orderNumber -> orderMapper.getOrderByOrderNumber(orderNumber)).collect(Collectors.toList());
                    OrderPaySuccessEvent orderPaySuccessEvent = new OrderPaySuccessEvent();
                    orderPaySuccessEvent.setOrderNumbers(Arrays.asList(orderNumbers));
                    eventPublisher.publishEvent(orderPaySuccessEvent);
                }
            }
        }
        return result.toString();
    }

    /**
     * 处理充值订单通知
     * 后续要传入虚拟发货物流信息给公众平台
     *
     * @param transaction
     * @param attach
     * @return
     * @author peiyuan.cai
     * @date 2024/1/24 14:39 星期三
     */
    private String handleNotifyBalanceOrder(Transaction transaction, String attach) {
        StringBuilder result = new StringBuilder();
        Date now = new Date();
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
                result.append("充值订单数据不存在 orderNumber = " + outTradeNo);
                return result.toString();
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
                    UserBalance userBalance = userBalanceService.getUserBalanceByUserId(userBalanceOrder.getUserId());
                    UserBalance userBalanceUpdate = new UserBalance();
                    userBalanceUpdate.setUserId(userBalanceOrder.getUserId());
                    userBalanceUpdate.setUpdateTime(now);
                    //设置最新余额
                    double newBalance = Arith.add(userBalance.getBalance(), userBalanceOrder.getTotal());
                    userBalanceUpdate.setBalance(newBalance);
                    boolean updateUserBalance = userBalanceService.updateById(userBalanceUpdate);
                    result.append("更新用户最新余额 ").append(" userId = ").append(userBalanceUpdate.getUserId()).append(" 最新余额 ").append(newBalance).append(" 更新结果 ").append(updateUserBalance).append("\n");

                    // 4、添加用户余额变化明细
                    UserBalanceDetail userBalanceDetail = new UserBalanceDetail();
                    userBalanceDetail.setUserId(userBalanceOrder.getUserId());
                    userBalanceDetail.setDetailType("1");
                    userBalanceDetail.setNewBalance(newBalance);
                    userBalanceDetail.setDescription("在线充值 " + NumberUtil.decimalFormat("#.##", userBalanceOrder.getTotal()) + " 最新余额 " + newBalance);
                    userBalanceDetail.setOrderNumber(userBalanceOrder.getOrderNumber());
                    userBalanceDetail.setUseTime(now);
                    userBalanceDetail.setUseTime(now);
                    // 消费是负数 充值是正数
                    userBalanceDetail.setUseBalance(userBalanceOrder.getTotal());

                    boolean saveUserBalanceDetail = userBalanceDetailService.save(userBalanceDetail);
                    result.append("添加用户余额变化明细 ").append(" 结果 ").append(saveUserBalanceDetail).append("\n");

                    // 充值订单支付成功通知事件 和监听此事件执行进一步的数据操作  如上传发货信息等
                    BalanceOrderPaySuccessEvent balanceOrderPaySuccessEvent = new BalanceOrderPaySuccessEvent();
                    balanceOrderPaySuccessEvent.setUserBalanceOrder(userBalanceOrder);
                    balanceOrderPaySuccessEvent.setOrderNumber(userBalanceOrder.getOrderNumber());
                    eventPublisher.publishEvent(balanceOrderPaySuccessEvent);
                }
            }
        }
        return result.toString();
    }

    /**
     * 普通订单
     * 创建微信预支付订单 并且返回支付参数
     *
     * @param userId
     * @param shopId
     * @param payParam
     * @return
     * @author peiyuan.cai
     * @date 2024/1/24 11:03 星期三
     */
    @Override
    public WechatPaySign createWeChatPrePayOrder(String userId, Long shopId, PayParam payParam) {
        WechatPaySign wechatPaySign = new WechatPaySign();

        //已经创建过支付订单 并且查询到了支付订单
        WxPayPrepay wxPayPrepayInDb = wxPayPrepayService.getOne(new LambdaQueryWrapper<WxPayPrepay>().eq(WxPayPrepay::getOrderNumbers, payParam.getOrderNumbers()));
        if (wxPayPrepayInDb != null) {
            wechatPaySign.setSign(wxPayPrepayInDb.getPrepaySign());
            wechatPaySign.setNonceStr(wxPayPrepayInDb.getPrepayNonce());
            wechatPaySign.setTimeStamp(wxPayPrepayInDb.getPrepayTimestamp());
            wechatPaySign.setPackageStr(wxPayPrepayInDb.getPrepayPackage());
            wechatPaySign.setPrepayId(wxPayPrepayInDb.getPrepayPackage());
        } else {
            // 没有创建过支付订单  向腾讯下单
            String[] orderNumbers = payParam.getOrderNumbers().split(StrUtil.COMMA);

            List<Order> orders = Arrays.asList(orderNumbers).stream().map(orderNumber -> orderMapper.getOrderByOrderNumber(orderNumber)).collect(Collectors.toList());

            Double actualTotal = 0D;
            StringBuilder prodName = new StringBuilder();

            for (Order order : orders) {
                actualTotal = Arith.add(actualTotal, order.getActualTotal());
                prodName.append(order.getProdName()).append(StrUtil.COMMA);
            }
            PrepayRequest request = new PrepayRequest();
            Amount amount = new Amount();
            amount.setTotal(Arith.toAmount(actualTotal));
            request.setAmount(amount);
            request.setAppid(WeChatPayUtil.appId);
            request.setMchid(WeChatPayUtil.merchantId);
            request.setDescription(prodName.substring(0, Math.min(100, prodName.length() - 1)));
            request.setNotifyUrl(WeChatPayUtil.WXPAY_NOTIFY_URL_TRANSACTION);
            //支付订单中使用新的id、？？？？ 因为orderNumber可能是有多个？合并付款的情况？
            String outTradeNo = String.valueOf(snowflake.nextId());
            request.setOutTradeNo(outTradeNo);
            Payer payer = new Payer();
            payer.setOpenid(userId);
            request.setPayer(payer);
            //通过返回的消息通知中attach字段判断支付订单类型
            request.setAttach(ORDER_TYPE_GOODS);
            PrepayWithRequestPaymentResponse response = WeChatPayUtil.jsapiServiceExtension.prepayWithRequestPayment(request);

            wechatPaySign.setSign(response.getPaySign());
            wechatPaySign.setNonceStr(response.getNonceStr());
            wechatPaySign.setTimeStamp(response.getTimeStamp());
            wechatPaySign.setPackageStr(response.getPackageVal());
            wechatPaySign.setPrepayId(response.getPackageVal());
            //保存预支付订单到数据库
            WxPayPrepay wxPayPrepay = wxPayPrepayService.saveWxPayPrepayGoods(payParam, request, response);
        }

        return wechatPaySign;
    }


    /**
     * 充值订单
     * 创建微信预支付订单 并且返回支付参数
     *
     * @param userBalanceOrder
     * @return
     * @author peiyuan.cai
     * @date 2024/1/23 13:03 星期二
     */
    @Override
    public WechatPaySign createWeChatPrePayOrder(UserBalanceOrder userBalanceOrder) {
        // request.setXxx(val)设置所需参数，具体参数可见Request定义
        PrepayRequest request = new PrepayRequest();
        Amount amount = new Amount();
        amount.setTotal(Arith.toAmount(userBalanceOrder.getActualTotal()));
        request.setAmount(amount);
        request.setAppid(WeChatPayUtil.appId);
        request.setMchid(WeChatPayUtil.merchantId);
        request.setDescription(userBalanceOrder.getProdName());
        request.setNotifyUrl(WeChatPayUtil.WXPAY_NOTIFY_URL_TRANSACTION);
        request.setOutTradeNo(userBalanceOrder.getOrderNumber());
        Payer payer = new Payer();
        payer.setOpenid(userBalanceOrder.getUserId());
        request.setPayer(payer);
        //通过返回的消息通知中attach字段判断支付订单类型
        request.setAttach(ORDER_TYPE_BALANCE);
        PrepayWithRequestPaymentResponse response = WeChatPayUtil.jsapiServiceExtension.prepayWithRequestPayment(request);

        WechatPaySign wechatPaySign = new WechatPaySign();
        wechatPaySign.setSign(response.getPaySign());
        wechatPaySign.setNonceStr(response.getNonceStr());
        wechatPaySign.setTimeStamp(response.getTimeStamp());
        wechatPaySign.setPackageStr(response.getPackageVal());
        wechatPaySign.setPrepayId(response.getPackageVal());
        //保存预支付订单到数据库
        wxPayPrepayService.saveWxPayPrepayUserBalance(userBalanceOrder, request, response);
        return wechatPaySign;
    }

    /**
     * 创建微信退款订单
     *
     * @param orderRefund
     * @return
     * @author peiyuan.cai
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public WxPayRefund createWeChatRefundOrder(OrderRefund orderRefund) {
        WxPayRefund wxPayRefund = new WxPayRefund();
        Date now = new Date();
        CreateRequest request = new CreateRequest();

        //【微信支付订单号】 原支付交易对应的微信订单号，与out_trade_no二选一
        request.setTransactionId(orderRefund.getBizPayNo());
        //【商户订单号】 原支付交易对应的商户订单号，与transaction_id二选一
        request.setOutTradeNo(orderRefund.getOrderPayNo());
        //【商户退款单号】 商户系统内部的退款单号，商户系统内部唯一，只能是数字、大小写字母_-|*@ ，同一退款单号多次请求只退一笔。
        request.setOutRefundNo(orderRefund.getOutRefundNo());
        //【退款原因】 若商户传入，会在下发给用户的退款消息中体现退款原因
        request.setReason(orderRefund.getRefundReason());
        if (StrUtil.isBlank(request.getReason())) {
            request.setReason(orderRefund.getRefundReason());
        }
        if (StrUtil.isBlank(request.getReason())) {
            request.setReason(orderRefund.getRefundReason());
        }
        //【退款结果回调url】 异步接收微信支付退款结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数。 如果参数中传了notify_url，则商户平台上配置的回调地址将不会生效，优先回调当前传的这个地址。
        request.setNotifyUrl(WeChatPayUtil.WXPAY_NOTIFY_URL_REFUND);

        /**
         * funds_account
         * 选填
         * string
         * 【退款资金来源】 若传递此参数则使用对应的资金账户退款，否则默认使用未结算资金退款（仅对老资金流商户适用）
         * 可选取值：
         * AVAILABLE: 仅对老资金流商户适用，指定从可用余额账户出资
         */
        AmountReq amount = new AmountReq();
        //【退款金额】 退款金额，单位为分，只能为整数，不能超过原订单支付金额。
        amount.setRefund(Arith.toAmount(orderRefund.getRefundAmount()).longValue());
        amount.setTotal(Arith.toAmount(orderRefund.getOrderAmount()).longValue());
        amount.setCurrency("CNY");
        request.setAmount(amount);
        try {
            // 调用接口
            Refund refund = WeChatPayUtil.refundService.create(request);

            /**
             * 保存退款订单信息
             */
            wxPayRefund.setAppId(WeChatPayUtil.appId);
            wxPayRefund.setMchId(WeChatPayUtil.merchantId);
            wxPayRefund.setNotifyUrl(request.getNotifyUrl());
            wxPayRefund.setCreateTime(now);
            wxPayRefund.setReason(orderRefund.getBuyerMsg());
            wxPayRefund.setTransactionId(request.getTransactionId());
            wxPayRefund.setOutTradeNo(request.getOutTradeNo());
            wxPayRefund.setOutRefundNo(request.getOutRefundNo());
            wxPayRefund.setAppId(WeChatPayUtil.appId);
            wxPayRefund.setTotalAmount(amount.getTotal());
            wxPayRefund.setRefundAmount(amount.getRefund());
            wxPayRefund.setGoodsDetail(Json.toJsonString(request.getGoodsDetail()));
            wxPayRefund.setFromAmount(Json.toJsonString(amount.getFrom()));
            wxPayRefund.setCurrency(amount.getCurrency());

            /**
             * 退款接口响应结果
             */
            wxPayRefund.setRefundId(refund.getRefundId());
            wxPayRefund.setRefundCreateTime(refund.getCreateTime());
            wxPayRefund.setSuccessTime(refund.getSuccessTime());
            wxPayRefund.setRefundCreateTime(refund.getCreateTime());
            wxPayRefund.setPromotionDetail(Json.toJsonString(refund.getPromotionDetail()));
            wxPayRefund.setResultAmount(Json.toJsonString(refund.getAmount()));
            wxPayRefund.setChannel(refund.getChannel().name());
            wxPayRefund.setStatus(refund.getStatus().name());
            //保存退款订单到数据库
            int save = wxPayRefundMapper.insert(wxPayRefund);
        } catch (Exception e) {
            throw new YamiShopBindException("退款操作失败 " + e.getLocalizedMessage());
        }

        return wxPayRefund;
    }

}
