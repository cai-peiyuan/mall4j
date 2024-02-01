

package com.yami.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.app.dto.DeliveryDto;
import com.yami.shop.bean.model.DeliveryOrder;
import com.yami.shop.bean.model.DeliveryOrderRoute;
import com.yami.shop.bean.model.Order;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.dao.DeliveryOrderMapper;
import com.yami.shop.dao.DeliveryOrderRouteMapper;
import com.yami.shop.dao.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yami.shop.bean.model.Delivery;
import com.yami.shop.dao.DeliveryMapper;

import com.yami.shop.service.DeliveryService;

import java.util.List;

/**
 *
 * @author lgh on 2018/11/26.
 */
@Service
public class DeliveryServiceImpl extends ServiceImpl<DeliveryMapper, Delivery> implements DeliveryService {

    @Autowired
    private DeliveryMapper deliveryMapper;
    @Autowired
    private DeliveryOrderMapper deliveryOrderMapper;
    @Autowired
    private DeliveryOrderRouteMapper deliveryOrderRouteMapper;
    @Autowired
    private OrderMapper orderMapper;

    /**
     * @param orderNumber
     * @return
     */
    @Override
    public DeliveryDto getDeliveryInfoByOrderNumber(String orderNumber) {
        Order order = orderMapper.getOrderByOrderNumber(orderNumber);
        if (order == null) {
            throw new YamiShopBindException("订单不存在");
        }
        Delivery delivery = deliveryMapper.selectById(order.getDvyId());
        DeliveryOrder deliveryOrder = deliveryOrderMapper.selectOne(new LambdaQueryWrapper<DeliveryOrder>().eq(DeliveryOrder::getExpressNumber, order.getDvyFlowId()));
        DeliveryDto deliveryDto = new DeliveryDto();
        deliveryDto.setDvyFlowId(order.getDvyFlowId());
        deliveryDto.setCompanyName(delivery.getDvyName());
        deliveryDto.setDeliveryOrder(deliveryOrder);
        List<DeliveryOrderRoute> list = deliveryOrderRouteMapper.selectList(new LambdaQueryWrapper<DeliveryOrderRoute>().eq(DeliveryOrderRoute::getExpressNumber, order.getDvyFlowId()).orderByAsc(DeliveryOrderRoute::getCreateTime));
        deliveryDto.setDeliveryRoutes(list);
        return deliveryDto;
    }

    /**
     * @param expressNumber
     * @return
     */
    @Override
    public DeliveryDto getDeliveryInfoByExpressNumber(String expressNumber) {
        return null;
    }
}
