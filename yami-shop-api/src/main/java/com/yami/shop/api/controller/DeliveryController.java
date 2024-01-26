package com.yami.shop.api.controller;

import cn.hutool.http.HttpUtil;
import com.yami.shop.bean.app.dto.DeliveryDto;
import com.yami.shop.bean.model.Delivery;
import com.yami.shop.bean.model.Order;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.Json;
import com.yami.shop.service.DeliveryService;
import com.yami.shop.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private OrderService orderService;

    /**
     * 查看物流接口
     */
    @GetMapping("/check")
    @Operation(summary = "查看物流", description = "根据订单号查看物流")
    @Parameter(name = "orderNumber", description = "订单号", required = true)
    public ServerResponseEntity<DeliveryDto> checkDelivery(String orderNumber) {

        Order order = orderService.getOrderByOrderNumber(orderNumber);
        if (order == null) {
            throw new YamiShopBindException("订单不存在");
        }
        Delivery delivery = deliveryService.getById(order.getDvyId());
        String url = delivery.getQueryUrl().replace("{dvyFlowId}", order.getDvyFlowId());
        String deliveryJson = HttpUtil.get(url);

        DeliveryDto deliveryDto = Json.parseObject(deliveryJson, DeliveryDto.class);
        deliveryDto.setDvyFlowId(order.getDvyFlowId());
        deliveryDto.setCompanyHomeUrl(delivery.getCompanyHomeUrl());
        deliveryDto.setCompanyName(delivery.getDvyName());
        return ServerResponseEntity.success(deliveryDto);
    }
}
