

package com.yami.shop.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.shop.bean.model.OrderRefund;
import com.yami.shop.bean.param.OrderRefundQueryParam;

/**
 * @author c'p'y
 */
public interface OrderRefundService extends IService<OrderRefund> {


    /**
     * 根据参数分页获取订单退款数据
     * @param page
     * @param orderRefundQueryParam
     * @return
     */
    IPage<OrderRefund> pageOrderRefundsDetailByOrderParam(Page<OrderRefund> page, OrderRefundQueryParam orderRefundQueryParam);

}
