

package com.yami.shop.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.shop.bean.model.Order;
import com.yami.shop.bean.model.OrderRefund;
import com.yami.shop.bean.param.OrderRefundParam;

/**
 * @author c'p'y
 */
public interface OrderRefundService extends IService<OrderRefund> {


    /**
     * 根据参数分页获取订单退款数据
     * @param page
     * @param orderRefundParam
     * @return
     */
    IPage<OrderRefund> pageOrderRefundsDetailByOrderParam(Page<OrderRefund> page, OrderRefundParam orderRefundParam);

    /**
     * 执行退款操作
     * @param refund
     */
    void refundAccept(OrderRefund refund);
    /**
     * 执行某一个购物订单的退款操作
     * @param order
     */
    void refundOrder(Order order);
}
