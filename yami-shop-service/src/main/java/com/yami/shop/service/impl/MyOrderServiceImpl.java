

package com.yami.shop.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.app.dto.MyOrderDto;
import com.yami.shop.bean.app.dto.OrderItemDto;
import com.yami.shop.bean.app.dto.OrderShopDto;
import com.yami.shop.bean.app.dto.UserAddrDto;
import com.yami.shop.bean.model.Order;
import com.yami.shop.bean.model.OrderItem;
import com.yami.shop.bean.model.ShopDetail;
import com.yami.shop.bean.model.UserAddrOrder;
import com.yami.shop.common.util.Arith;
import com.yami.shop.common.util.PageAdapter;
import com.yami.shop.dao.OrderMapper;
import com.yami.shop.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author lgh on 2018/09/15.
 */
@Service
public class MyOrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements MyOrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserAddrOrderService userAddrOrderService;
    @Autowired
    private ShopDetailService shopDetailService;
    @Autowired
    private OrderItemService orderItemService;

    @Override
    public IPage<MyOrderDto> pageMyOrderByUserIdAndStatus(Page<MyOrderDto> page, String userId, Integer status) {
        page.setRecords(orderMapper.listMyOrderByUserIdAndStatus(new PageAdapter(page), userId, status));
        page.setTotal(orderMapper.countMyOrderByUserIdAndStatus(userId, status));
        return page;
    }

    /**
     * 根据用户名和订单号码查询订单信息
     *
     * @param userId
     * @param orderNumber
     * @return
     * @author peiyuan.cai
     * @date 2024/1/24 17:13 星期三
     */
    @Override
    public OrderShopDto getMyOrderByOrderNumber(String userId, String orderNumber) {
        OrderShopDto orderShopDto = new OrderShopDto();

        Order order = orderService.getOrderByOrderNumber(orderNumber);

        if (order == null) {
            throw new RuntimeException("该订单不存在");
        }
        if (!Objects.equals(order.getUserId(), userId)) {
            throw new RuntimeException("你没有权限获取该订单信息");
        }

        ShopDetail shopDetail = shopDetailService.getShopDetailByShopId(order.getShopId());
        UserAddrOrder userAddrOrder = userAddrOrderService.getById(order.getAddrOrderId());
        UserAddrDto userAddrDto = BeanUtil.copyProperties(userAddrOrder, UserAddrDto.class);
        List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderNumber(orderNumber);
        List<OrderItemDto> orderItemList = BeanUtil.copyToList(orderItems, OrderItemDto.class);

        orderShopDto.setShopId(shopDetail.getShopId());
        orderShopDto.setShopName(shopDetail.getShopName());
        orderShopDto.setActualTotal(order.getActualTotal());
        orderShopDto.setUserAddrDto(userAddrDto);
        orderShopDto.setOrderItemDtos(orderItemList);
        orderShopDto.setTransfee(order.getFreightAmount());
        orderShopDto.setReduceAmount(order.getReduceAmount());
        orderShopDto.setCreateTime(order.getCreateTime());
        orderShopDto.setRemarks(order.getRemarks());
        orderShopDto.setStatus(order.getStatus());

        double total = 0.0;
        Integer totalNum = 0;
        for (OrderItemDto orderItem : orderShopDto.getOrderItemDtos()) {
            total = Arith.add(total, orderItem.getProductTotalAmount());
            totalNum += orderItem.getProdCount();
        }
        orderShopDto.setTotal(total);
        orderShopDto.setTotalNum(totalNum);
        return orderShopDto;
    }

    /**
     * 根据用户名和订单号码查询订单信息 v2
     *
     * @param userId
     * @param orderNumber
     * @return
     * @author peiyuan.cai
     * @date 2024/1/24 17:16 星期三
     */
    @Override
    public JSONObject getMyOrderByOrderNumberV2(String userId, String orderNumber) {
        JSONObject object = new JSONObject();
        object.put("orderShop", getMyOrderByOrderNumber(userId, orderNumber));
        object.put("order", orderService.getOrderByOrderNumber(orderNumber));
        return object;
    }

}
