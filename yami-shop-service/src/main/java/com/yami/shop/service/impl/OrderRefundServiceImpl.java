

package com.yami.shop.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.model.OrderRefund;
import com.yami.shop.bean.param.OrderRefundQueryParam;
import com.yami.shop.dao.OrderMapper;
import com.yami.shop.dao.OrderRefundMapper;
import com.yami.shop.service.OrderRefundService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author c'p'y
 */
@Service
@AllArgsConstructor
public class OrderRefundServiceImpl extends ServiceImpl<OrderRefundMapper, OrderRefund> implements OrderRefundService {
    private final OrderMapper orderMapper;
    private final OrderRefundMapper orderRefundMapper;

    /**
     * 根据参数分页获取订单退款数据
     *
     * @param page
     * @param orderRefundQueryParam
     * @return
     */
    @Override
    public IPage<OrderRefund> pageOrderRefundsDetailByOrderParam(Page<OrderRefund> page, OrderRefundQueryParam orderRefundQueryParam) {
       return null;
    }
}
