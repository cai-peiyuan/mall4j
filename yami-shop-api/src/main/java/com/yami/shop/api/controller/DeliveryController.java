package com.yami.shop.api.controller;

import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yami.shop.bean.app.dto.DeliveryDto;
import com.yami.shop.bean.app.dto.DeliveryInfoDto;
import com.yami.shop.bean.model.Delivery;
import com.yami.shop.bean.model.DeliveryOrder;
import com.yami.shop.bean.model.DeliveryOrderRoute;
import com.yami.shop.bean.model.Order;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.Json;
import com.yami.shop.service.DeliveryOrderRouteService;
import com.yami.shop.service.DeliveryOrderService;
import com.yami.shop.service.DeliveryService;
import com.yami.shop.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lanhai
 */
@RestController
@RequestMapping("/delivery")
@Tag(name = "查看物流接口")
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;
    @Autowired
    private DeliveryOrderService deliveryOrderService;
    @Autowired
    private DeliveryOrderRouteService deliveryOrderRouteService;
    @Autowired
    private OrderService orderService;

    /**
     * 查看物流接口
     */
    @GetMapping("/check/{orderNumber}")
    @Operation(summary = "查看物流", description = "根据订单号查看物流")
    @Parameter(name = "orderNumber", description = "订单号", required = true)
    public ServerResponseEntity<DeliveryDto> checkDelivery(@PathVariable(name="orderNumber") String orderNumber) {

        Order order = orderService.getOrderByOrderNumber(orderNumber);
        if (order == null) {
            throw new YamiShopBindException("订单不存在");
        }
        Delivery delivery = deliveryService.getById(order.getDvyId());
        DeliveryOrder deliveryOrder = deliveryOrderService.getOne(new LambdaQueryWrapper<DeliveryOrder>().eq(DeliveryOrder::getExpressNumber, order.getDvyFlowId()));
        DeliveryDto deliveryDto = new DeliveryDto();
        deliveryDto.setDvyFlowId(order.getDvyFlowId());
        deliveryDto.setCompanyName(delivery.getDvyName());

        deliveryDto.setDeliveryOrder(deliveryOrder);

        List<DeliveryOrderRoute> list = deliveryOrderRouteService.list(new LambdaQueryWrapper<DeliveryOrderRoute>().eq(DeliveryOrderRoute::getExpressNumber, order.getDvyFlowId()).orderByAsc(DeliveryOrderRoute::getCreateTime));
        deliveryDto.setDeliveryRoutes(list);
        /*
        String url = delivery.getQueryUrl().replace("{dvyFlowId}", order.getDvyFlowId());
        String deliveryJson = HttpUtil.get(url);

        DeliveryDto deliveryDto = Json.parseObject(deliveryJson, DeliveryDto.class);
        deliveryDto.setDvyFlowId(order.getDvyFlowId());
        deliveryDto.setCompanyHomeUrl(delivery.getCompanyHomeUrl());
        deliveryDto.setCompanyName(delivery.getDvyName());*/
        return ServerResponseEntity.success(deliveryDto);
    }
}
