

package com.yami.shop.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.app.dto.MyOrderDto;
import com.yami.shop.bean.app.dto.OrderItemDto;
import com.yami.shop.bean.app.dto.OrderShopDto;
import com.yami.shop.bean.app.dto.UserAddrDto;
import com.yami.shop.bean.model.*;
import com.yami.shop.common.exception.YamiShopBindException;
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

    @Autowired
    private DeliveryOrderService deliveryOrderService;

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private DeliveryUserService deliveryUserService;
    @Autowired
    private UserBalanceService userBalanceService;

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
        Order order = orderService.getOrderByOrderNumber(orderNumber);

        if (order == null) {
            throw new YamiShopBindException("该订单不存在");
        }
        if (!Objects.equals(order.getUserId(), userId)) {
            throw new YamiShopBindException("没有权限获取该订单信息");
        }
        return getOrderShopDtoByOrder(order);
    }

    /**
     * 根据数据库订单存储的数据组件订单数据
     *
     * @param order
     * @return
     */
    private OrderShopDto getOrderShopDtoByOrder(Order order) {
        OrderShopDto orderShopDto = new OrderShopDto();
        ShopDetail shopDetail = shopDetailService.getShopDetailByShopId(order.getShopId());
        UserAddrOrder userAddrOrder = userAddrOrderService.getById(order.getAddrOrderId());
        UserAddrDto userAddrDto = BeanUtil.copyProperties(userAddrOrder, UserAddrDto.class);
        List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderNumber(order.getOrderNumber());
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
     * @param isStaff
     * @return
     * @author peiyuan.cai
     * @date 2024/1/24 17:16 星期三
     */
    @Override
    public JSONObject getMyOrderDetailByOrderNumberV2(String userId, String orderNumber, Integer isStaff) {
        JSONObject object = new JSONObject();
        Order order = orderService.getOrderByOrderNumber(orderNumber);
        DeliveryOrder deliveryOrder = deliveryOrderService.getOne(new LambdaQueryWrapper<DeliveryOrder>().eq(DeliveryOrder::getExpressNumber, order.getDvyFlowId()));
        object.put("orderShop", getOrderShopDtoByOrder(order));
        object.put("order", order);
        object.put("deliveryOrder", deliveryOrder);
        if (isStaff != null && isStaff == 1) {
            //附带 物流公司 和配送员信息
            List<Delivery> deliveryList = deliveryService.list();
            object.put("deliveryList", deliveryList);
            List<DeliveryUser> deliveryUserList = deliveryUserService.list();
            object.put("deliveryUserList", deliveryUserList);
        }
        object.put("userBalance", userBalanceService.getUserBalanceByUserId(userId));
        return object;
    }

}
