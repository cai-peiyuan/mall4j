

package com.yami.shop.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qq.wechat.pay.WeChatPayUtil;
import com.qq.wechat.pay.config.WechatPaySign;
import com.wechat.pay.java.service.partnerpayments.jsapi.model.Transaction;
import com.wechat.pay.java.service.payments.jsapi.model.Amount;
import com.wechat.pay.java.service.payments.jsapi.model.Payer;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
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
import com.yami.shop.dao.OrderSettlementMapper;
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
    private OrderService orderService;

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
                    userBalanceDetail.setDescription("在线充值 " + NumberUtil.decimalFormat("#.##", userBalanceOrder.getTotal()) + " 最新余额 "+ newBalance);
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

        WechatPaySign wechatPaySign = new WechatPaySign();
        wechatPaySign.setSign(response.getPaySign());
        wechatPaySign.setNonceStr(response.getNonceStr());
        wechatPaySign.setTimeStamp(response.getTimeStamp());
        wechatPaySign.setPackageStr(response.getPackageVal());
        wechatPaySign.setPrepayId(response.getPackageVal());
        //保存预支付订单到数据库
        WxPayPrepay wxPayPrepay = wxPayPrepayService.saveWxPayPrepayGoods(payParam, request, response);
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

}
