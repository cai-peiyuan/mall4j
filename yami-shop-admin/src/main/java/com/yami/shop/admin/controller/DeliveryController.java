package com.yami.shop.admin.controller;

import com.yami.shop.bean.app.dto.DeliveryDto;
import com.yami.shop.bean.model.Delivery;
import com.yami.shop.bean.model.DeliveryUser;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.service.DeliveryService;
import com.yami.shop.service.DeliveryUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author lgh on 2018/11/26.
 */
@RestController
@RequestMapping("/admin/delivery")
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private DeliveryUserService deliveryUserService;

    /**
     * 获取所有物流公司名称
     */
    @GetMapping("/list")
    public ServerResponseEntity<List<Delivery>> list() {
        List<Delivery> list = deliveryService.list();
        return ServerResponseEntity.success(list);
    }

    /**
     * 获取配送员信息
     *
     * @author cpy
     */
    @GetMapping("/getDeliveryUsers")
    public ServerResponseEntity<Object> getDeliveryUsers() {
        List<DeliveryUser> list = deliveryUserService.list();
        return ServerResponseEntity.success(list);
    }

    /**
     * 获取配送员信息
     *
     * @author cpy
     */
    @GetMapping("/info/{orderNumber}")
    public ServerResponseEntity<DeliveryDto> info(@PathVariable(name = "orderNumber") String orderNumber) {
        DeliveryDto deliveryDto = deliveryService.getDeliveryInfoByOrderNumber(orderNumber);
        return ServerResponseEntity.success(deliveryDto);
    }
}
