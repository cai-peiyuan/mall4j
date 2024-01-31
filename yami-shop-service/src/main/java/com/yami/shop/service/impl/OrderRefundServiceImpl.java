package com.yami.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.model.Order;
import com.yami.shop.bean.model.OrderRefund;
import com.yami.shop.bean.model.WxPayRefund;
import com.yami.shop.bean.param.OrderRefundQueryParam;
import com.yami.shop.dao.OrderMapper;
import com.yami.shop.dao.OrderRefundMapper;
import com.yami.shop.service.OrderRefundService;
import com.yami.shop.service.PayService;
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

    /**
     * 根据参数分页获取订单退款数据
     *
     * @param page
     * @param orderRefundQueryParam
     * @return
     */
    @Override
    public IPage<OrderRefund> pageOrderRefundsDetailByOrderParam(Page<OrderRefund> page, OrderRefundQueryParam orderRefundQueryParam) {
        return page(page, new LambdaQueryWrapper<OrderRefund>().eq(StringUtils.isNotBlank(orderRefundQueryParam.getOrderNumber()), OrderRefund::getOrderNumber, orderRefundQueryParam.getOrderNumber()));
    }

    /**
     * 执行退款操作
     *
     * @param refund
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refundAccept(OrderRefund refund) {
        WxPayRefund weChatRefundOrder = payService.createWeChatRefundOrder(refund);
        //处理状态:1为待审核,2为同意,3为不同意
        refund.setRefundSts(2);
        refund.setReturnMoneySts(0);
        refund.setHandelTime(new Date());
        refund.setSellerMsg(refund.getSellerMsg());
        updateById(refund);
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
