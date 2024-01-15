/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */

package com.yami.shop.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.yami.shop.bean.app.dto.OrderCountData;
import com.yami.shop.bean.app.dto.ShopCartOrderMergerDto;
import com.yami.shop.bean.app.dto.UserInfoDto;
import com.yami.shop.bean.event.CancelOrderEvent;
import com.yami.shop.bean.event.ReceiptOrderEvent;
import com.yami.shop.bean.event.SubmitOrderEvent;
import com.yami.shop.bean.model.Order;
import com.yami.shop.bean.model.OrderItem;
import com.yami.shop.bean.model.UserAddrOrder;
import com.yami.shop.bean.param.OrderParam;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.util.PageAdapter;
import com.yami.shop.dao.*;
import com.yami.shop.service.OrderItemService;
import com.yami.shop.service.OrderService;
import com.yami.shop.service.UserAddrOrderService;
import com.yami.shop.service.UserService;
import com.yly.print_sdk_library.RequestMethod;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lgh on 2018/09/15.
 */
@Service
@AllArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final UserService userService;

    private final OrderMapper orderMapper;

    private final CommonMapper commonMapper;

    private final SkuMapper skuMapper;

    private final OrderItemMapper orderItemMapper;

    private final ProductMapper productMapper;

    private final ApplicationEventPublisher eventPublisher;

    private final UserAddrOrderService userAddrOrderService;

    private final OrderItemService orderItemService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Order getOrderByOrderNumber(String orderNumber) {
        return orderMapper.getOrderByOrderNumber(orderNumber);
    }

    @Override
    @CachePut(cacheNames = "ConfirmOrderCache", key = "#userId")
    public ShopCartOrderMergerDto putConfirmOrderCache(String userId, ShopCartOrderMergerDto shopCartOrderMergerDto) {
        return shopCartOrderMergerDto;
    }

    @Override
    @Cacheable(cacheNames = "ConfirmOrderCache", key = "#userId")
    public ShopCartOrderMergerDto getConfirmOrderCache(String userId) {
        return null;
    }

    @Override
    @CacheEvict(cacheNames = "ConfirmOrderCache", key = "#userId")
    public void removeConfirmOrderCache(String userId) {
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Order> submit(String userId, ShopCartOrderMergerDto mergerOrder) {
        List<Order> orderList = new ArrayList<>();
        // 通过事务提交订单
        eventPublisher.publishEvent(new SubmitOrderEvent(mergerOrder, orderList));

        // 插入订单
        saveBatch(orderList);
        List<OrderItem> orderItems = orderList.stream().flatMap(order -> order.getOrderItems().stream()).collect(Collectors.toList());
        // 插入订单项，返回主键
        orderItemMapper.insertBatch(orderItems);
        return orderList;
    }


    /**
     * 订单发货
     *
     * @param order
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delivery(Order order) {
        orderMapper.updateById(order);
        // 发送用户发货通知
        Map<String, String> params = new HashMap<>(16);
        params.put("orderNumber", order.getOrderNumber());
        //		Delivery delivery = deliveryMapper.selectById(order.getDvyId());
        //		params.put("dvyName", delivery.getDvyName());
        //		params.put("dvyFlowId", order.getDvyFlowId());
        //		smsLogService.sendSms(SmsType.NOTIFY_DVY, order.getUserId(), order.getMobile(), params);

    }

    @Override
    public List<Order> listOrderAndOrderItems(Integer orderStatus, DateTime lessThanUpdateTime) {
        return orderMapper.listOrderAndOrderItems(orderStatus, lessThanUpdateTime);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrders(List<Order> orders) {

        orderMapper.cancelOrders(orders);
        List<OrderItem> allOrderItems = new ArrayList<>();
        for (Order order : orders) {
            List<OrderItem> orderItems = order.getOrderItems();
            allOrderItems.addAll(orderItems);
            eventPublisher.publishEvent(new CancelOrderEvent(order));
        }
        if (CollectionUtil.isEmpty(allOrderItems)) {
            return;
        }
        Map<Long, Integer> prodCollect = new HashMap<>(16);
        Map<Long, Integer> skuCollect = new HashMap<>(16);

        allOrderItems.stream().collect(Collectors.groupingBy(OrderItem::getProdId)).forEach((prodId, orderItems) -> {
            int prodTotalNum = orderItems.stream().mapToInt(OrderItem::getProdCount).sum();
            prodCollect.put(prodId, prodTotalNum);
        });
        productMapper.returnStock(prodCollect);

        allOrderItems.stream().collect(Collectors.groupingBy(OrderItem::getSkuId)).forEach((skuId, orderItems) -> {
            int prodTotalNum = orderItems.stream().mapToInt(OrderItem::getProdCount).sum();
            skuCollect.put(skuId, prodTotalNum);
        });
        skuMapper.returnStock(skuCollect);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmOrder(List<Order> orders) {
        orderMapper.confirmOrder(orders);
        for (Order order : orders) {
            eventPublisher.publishEvent(new ReceiptOrderEvent(order));
        }
    }

    @Override
    public List<Order> listOrdersDetailByOrder(Order order, Date startTime, Date endTime) {
        return orderMapper.listOrdersDetailByOrder(order, startTime, endTime);
    }

    @Override
    public IPage<Order> pageOrdersDetailByOrderParam(Page<Order> page, OrderParam orderParam) {
        page.setRecords(orderMapper.listOrdersDetailByOrderParam(new PageAdapter(page), orderParam));
        page.setTotal(orderMapper.countOrderDetail(orderParam));
        for (Order order : page.getRecords()) {
            order.setUserInfo(userService.getUserInfoById(order.getUserId()));
        }
        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrders(List<Order> orders) {
        orderMapper.deleteOrders(orders);
    }

    @Override
    public OrderCountData getOrderCount(String userId) {
        return orderMapper.getOrderCount(userId);
    }

    /**
     * 打印订单信息
     *
     * @param order
     * @return
     */
    @Override
    public String printOrder(Order order) {
        String print_order_auto = (String) redisTemplate.opsForHash().get("sys:config", "print_order_auto");
        String print_machine_code = (String) redisTemplate.opsForHash().get("sys:config", "print_machine_code");
        String print_order_content_template = (String) redisTemplate.opsForHash().get("sys:config", "print_order_content_template");

        if (StrUtil.isBlank(print_machine_code)) {
            throw new YamiShopBindException("请在系统参数中设置系统打印机设备编码[" + print_machine_code + "]");
        }
        String printer = (String) redisTemplate.opsForHash().get("sys:printer", print_machine_code);

        if (StrUtil.isBlank(printer)) {
            throw new YamiShopBindException("请在系统打印机中设置系统打印机设备编码[" + print_machine_code + "]");
        }

        if (StrUtil.isBlank(print_order_content_template)) {
            throw new YamiShopBindException("请在系统参数中设置系统订单打印模板[" + print_order_content_template + "]");
        }

        JSONObject jsonObject = JSON.parseObject(printer);
        String accessToken = jsonObject.getString("accessToken");
        String clientId = jsonObject.getString("clientId");
        String clientSecret = jsonObject.getString("clientSecret");
        RequestMethod.init(clientId, clientSecret);
        RequestMethod instance = RequestMethod.getInstance();
        try {
            //{"error":0,"error_description":"success","timestamp":1705251051,"body":{"id":2540003905,"origin_id":"1"}}
            String printResult = instance.printIndex(accessToken, print_machine_code, print_order_content_template, order.getOrderNumber());
            JSONObject jsonObject1 = JSON.parseObject(printResult);
            JSONObject body = jsonObject1.getJSONObject("body");
            int errorCode = jsonObject1.getInteger("error");
            if (errorCode == 0 && body != null) {
                String printId = body.getString("id");
                String origin_id = body.getString("origin_id");
                // 更新订单打印次数
                orderMapper.updateOrdersPrintTimes(Lists.newArrayList(order));
                // 添加打印日志
                Map printLogMap = new HashMap();
                printLogMap.put("print_content", print_order_content_template);
                printLogMap.put("origin_id", origin_id);
                printLogMap.put("machine_code", print_machine_code);
                printLogMap.put("print_id", printId);
                printLogMap.put("order_number", order.getOrderNumber());
                List<Map> maps = Lists.newArrayList(printLogMap);
                commonMapper.addPrinterLogs(maps);
            }
            return printResult;
        } catch (Exception e) {
            e.printStackTrace();
            throw new YamiShopBindException("打印订单出错，订单编号[" + order.getOrderNumber() + "]，错误信息[" + e.getLocalizedMessage() + "]");
        }
    }

    /**
     * 设置订单附加信息
     *
     * @param order
     */
    @Override
    public void setOrderExtraInfo(Order order) {
        //订单商品信息
        List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderNumber(order.getOrderNumber());
        order.setOrderItems(orderItems);
        //订单收货地址
        UserAddrOrder userAddrOrder = userAddrOrderService.getById(order.getAddrOrderId());
        order.setUserAddrOrder(userAddrOrder);
        //订单用户信息
        UserInfoDto userInfoDto = userService.getUserInfoById(order.getUserId());
        order.setUserInfo(userInfoDto);
    }

}
