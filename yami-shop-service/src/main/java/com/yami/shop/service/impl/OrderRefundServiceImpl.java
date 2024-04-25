package com.yami.shop.service.impl;

import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.enums.PayType;
import com.yami.shop.bean.model.*;
import com.yami.shop.bean.param.OrderRefundParam;
import com.yami.shop.common.util.Arith;
import com.yami.shop.common.util.Json;
import com.yami.shop.dao.OrderMapper;
import com.yami.shop.dao.OrderRefundMapper;
import com.yami.shop.dao.OrderSettlementMapper;
import com.yami.shop.service.OrderRefundService;
import com.yami.shop.service.PayService;
import com.yami.shop.service.UserBalanceDetailService;
import com.yami.shop.service.UserBalanceService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author c'p'y
 */
@Service
@AllArgsConstructor
public class OrderRefundServiceImpl extends ServiceImpl<OrderRefundMapper, OrderRefund> implements OrderRefundService {
    private final OrderMapper orderMapper;
    private final OrderRefundMapper orderRefundMapper;
    private final PayService payService;
    private final UserBalanceService userBalanceService;
    private final UserBalanceDetailService userBalanceDetailService;
    private final OrderSettlementMapper orderSettlementMapper;

    /**
     * 根据参数分页获取订单退款数据
     *
     * @param page
     * @param orderRefundParam
     * @return
     */
    @Override
    public IPage<OrderRefund> pageOrderRefundsDetailByOrderParam(Page<OrderRefund> page, OrderRefundParam orderRefundParam) {
        return page(page, new LambdaQueryWrapper<OrderRefund>().eq(StringUtils.isNotBlank(orderRefundParam.getOrderNumber()), OrderRefund::getOrderNumber, orderRefundParam.getOrderNumber()));
    }

    /**
     * 执行退款操作
     * 同意退款
     *
     * @param refund
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refundAccept(OrderRefund refund) {
        Date now = new Date();
        if (refund.getPayType() == PayType.BALANCE.value()) {
            //余额支付 退还余额
            /**
             * 余额支付扣款
             */
            // 3、更新用户充值余额值
            UserBalance userBalance = userBalanceService.getUserBalanceByUserId(refund.getUserId());

            UserBalance userBalanceUpdate = new UserBalance();
            userBalanceUpdate.setUserId(refund.getUserId());
            userBalanceUpdate.setUpdateTime(now);
            //设置最新余额
            double newBalance = Arith.add(userBalance.getBalance(), refund.getRefundAmount());
            userBalanceUpdate.setBalance(newBalance);

            boolean updateUserBalance = userBalanceService.updateById(userBalanceUpdate);

            StringBuilder result = new StringBuilder();
            result.append("更新用户最新余额 ").append(" userId = ").append(userBalanceUpdate.getUserId()).append(" 最新余额 ").append(newBalance).append(" 更新结果 ").append(updateUserBalance).append("\n");
            log.debug(result.toString());

            // 4、添加用户余额变化明细
            UserBalanceDetail userBalanceDetail = new UserBalanceDetail();
            userBalanceDetail.setUserId(refund.getUserId());
            userBalanceDetail.setDetailType("1");
            userBalanceDetail.setNewBalance(newBalance);
            userBalanceDetail.setDescription("订单退款 " + NumberUtil.decimalFormat("#.##", refund.getRefundAmount()) + " 最新余额 " + newBalance);
            userBalanceDetail.setOrderNumber(refund.getOrderNumber());
            userBalanceDetail.setUseTime(now);
            // 消费是负数 充值是正数
            userBalanceDetail.setUseBalance(refund.getRefundAmount());

            boolean saveUserBalanceDetail = userBalanceDetailService.save(userBalanceDetail);

            result.append("添加用户余额变化明细 ").append(" 结果 ").append(saveUserBalanceDetail).append("\n");

            log.debug(result.toString());

            // 修改订单信息
            OrderSettlement orderSettlement = new OrderSettlement();
            orderSettlement.setPayNo(refund.getOrderPayNo());
            orderSettlement.setBizPayNo(null);
            orderSettlement.setPayType(PayType.BALANCE.value());
            orderSettlement.setUserId(refund.getUserId());
            orderSettlement.setOrderNumber(refund.getOrderNumber());
            orderSettlement.setPayStatus(-1);
            orderSettlement.setClearingTime(now);
            orderSettlement.setIsClearing(1);
            //更新订单结算信息
            int update = orderSettlementMapper.updateByOrderNumberAndUserId(orderSettlement);
            result.append("更新订单结算信息 ").append(" orderNumber = ").append(refund.getOrderNumber()).append(" 更新结果 ").append(update).append("\n");
            // Order order = orderMapper.getOrderByOrderNumber(orderNumber);

            // 将订单改为已关闭
            Order order = orderMapper.getOrderByOrderNumber(refund.getOrderNumber());
            if (order != null) {
                Order updateOrder = new Order();
                updateOrder.setOrderId(order.getOrderId());
                updateOrder.setCloseType(2);
                updateOrder.setRefundSts(2);
                updateOrder.setFinallyTime(now);
                updateOrder.setUpdateTime(now);
                updateOrder.setStatus(6);
                updateOrder.setRemarks("买家备注：" + order.getRemarks() + " 商家备注：订单退款已到账 退款交易单号" + refund.getRefundId());
                int updateOrderResult = orderMapper.updateById(updateOrder);
                result.append("已更新购物订单信息 ").append(" orderNumber = ").append(refund.getOrderNumber()).append(" 更新结果 ").append(updateOrderResult).append("\n");
            } else {
                result.append("未查询到购物订单信息 ").append(" orderNumber = ").append(refund.getOrderNumber()).append("\n");
            }
            result.append("更新订单信息 ").append(" orderNumbers = ").append(Json.toJsonString(refund.getOrderNumber())).append(" 更新结果 ").append(update).append("\n");

            //处理状态:1为待审核,2为同意,3为不同意
            refund.setRefundSts(2);
            refund.setReturnMoneySts(1);
            refund.setHandelTime(new Date());
            refund.setSellerMsg(refund.getSellerMsg());
            updateById(refund);

        } else if (refund.getPayType() == PayType.WECHATPAY.value()) {
            // 微信支付 发起退款
            WxPayRefund weChatRefundOrder = payService.createWeChatRefundOrder(refund);
            //处理状态:1为待审核,2为同意,3为不同意
            refund.setRefundSts(2);
            refund.setReturnMoneySts(0);
            refund.setHandelTime(new Date());
            refund.setSellerMsg(refund.getSellerMsg());
            updateById(refund);

        }
    }

    /**
     * 执行某一个购物订单的退款操作
     *
     * @param order
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refundOrder(Order order) {

    }
}
