

package com.yami.shop.api.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yami.shop.bean.app.dto.*;
import com.yami.shop.bean.enums.OrderStatus;
import com.yami.shop.bean.model.Order;
import com.yami.shop.bean.model.OrderItem;
import com.yami.shop.bean.model.ShopDetail;
import com.yami.shop.bean.model.UserAddrOrder;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.Arith;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.security.api.util.SecurityUtils;
import com.yami.shop.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author lanhai
 */
@RestController
@RequestMapping("/p/myOrder")
@Tag(name = "我的订单接口")
@AllArgsConstructor
public class MyOrderController {

    private final OrderService orderService;


    private final UserAddrOrderService userAddrOrderService;

    private final ProductService productService;

    private final SkuService skuService;

    private final MyOrderService myOrderService;

    private final ShopDetailService shopDetailService;

    private final OrderItemService orderItemService;


    /**
     * 订单详情信息接口
     */
    @GetMapping("/orderDetail")
    @Operation(summary = "订单详情信息", description = "根据订单号获取订单详情信息")
    @Parameter(name = "orderNumber", description = "订单号", required = true)
    public ServerResponseEntity<OrderShopDto> orderDetail(@RequestParam(value = "orderNumber") String orderNumber) {
        String userId = SecurityUtils.getUser().getUserId();
        OrderShopDto orderShopDto = myOrderService.getMyOrderByOrderNumber(userId, orderNumber);
        return ServerResponseEntity.success(orderShopDto);
    }

    /**
     * 订单详情信息接口
     */
    @GetMapping("/orderDetail/{orderNumber}")
    @Operation(summary = "订单详情信息", description = "根据订单号获取订单详情信息")
    public ServerResponseEntity<Object> orderDetailV2(@PathVariable(value = "orderNumber") String orderNumber) {
        String userId = SecurityUtils.getUser().getUserId();
        JSONObject orderInfo = myOrderService.getMyOrderByOrderNumberV2(userId, orderNumber);
        return ServerResponseEntity.success(orderInfo);
    }


    /**
     * 订单列表接口
     */
    @GetMapping("/myOrder")
    @Operation(summary = "订单列表信息", description = "根据订单状态获取订单列表信息，状态为0时获取所有订单")
    @Parameters({
            @Parameter(name = "status", description = "订单状态 1:待付款 2:待发货 3:待收货 4:待评价 5:成功 6:失败")
    })
    public ServerResponseEntity<IPage<MyOrderDto>> myOrder(@RequestParam(value = "status") Integer status, PageParam<MyOrderDto> page) {
        String userId = SecurityUtils.getUser().getUserId();
        IPage<MyOrderDto> myOrderDtoIpage = myOrderService.pageMyOrderByUserIdAndStatus(page, userId, status);
        return ServerResponseEntity.success(myOrderDtoIpage);
    }

    /**
     * 订单退款
     */
    @PutMapping("/refundApply/{orderNumber}")
    @Operation(summary = "根据订单号申请退款", description = "根据订单号申请退款")
    @Parameter(name = "orderNumber", description = "订单号", required = true)
    public ServerResponseEntity<String> refundApply(@PathVariable("orderNumber") String orderNumber) {
        String userId = SecurityUtils.getUser().getUserId();
        Order order = orderService.getOrderByOrderNumber(orderNumber);
        if (!Objects.equals(order.getUserId(), userId)) {
            throw new YamiShopBindException("你没有权限操作该订单");
        }
        if (!Objects.equals(order.getStatus(), OrderStatus.PADYED.value())) {
            throw new YamiShopBindException("订单状态不正确，无法申请退款");
        }
        List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderNumber(orderNumber);
        order.setOrderItems(orderItems);
        // 取消订单
        orderService.refundApplyOrders(Collections.singletonList(order));

        // 清除缓存
        for (OrderItem orderItem : orderItems) {
            productService.removeProductCacheByProdId(orderItem.getProdId());
            skuService.removeSkuCacheBySkuId(orderItem.getSkuId(), orderItem.getProdId());
        }
        return ServerResponseEntity.success();
    }
/**
     * 取消订单
     */
    @PutMapping("/cancel/{orderNumber}")
    @Operation(summary = "根据订单号取消订单", description = "根据订单号取消订单")
    @Parameter(name = "orderNumber", description = "订单号", required = true)
    public ServerResponseEntity<String> cancel(@PathVariable("orderNumber") String orderNumber) {
        String userId = SecurityUtils.getUser().getUserId();
        Order order = orderService.getOrderByOrderNumber(orderNumber);
        if (!Objects.equals(order.getUserId(), userId)) {
            throw new YamiShopBindException("你没有权限获取该订单信息");
        }
        if (!Objects.equals(order.getStatus(), OrderStatus.UNPAY.value())) {
            throw new YamiShopBindException("订单已支付，无法取消订单");
        }
        List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderNumber(orderNumber);
        order.setOrderItems(orderItems);
        // 取消订单
        orderService.cancelOrders(Collections.singletonList(order));

        // 清除缓存
        for (OrderItem orderItem : orderItems) {
            productService.removeProductCacheByProdId(orderItem.getProdId());
            skuService.removeSkuCacheBySkuId(orderItem.getSkuId(), orderItem.getProdId());
        }
        return ServerResponseEntity.success();
    }

    /**
     * 确认收货
     */
    @PutMapping("/receipt/{orderNumber}")
    @Operation(summary = "根据订单号确认收货", description = "根据订单号确认收货")
    public ServerResponseEntity<String> receipt(@PathVariable("orderNumber") String orderNumber) {
        String userId = SecurityUtils.getUser().getUserId();
        Order order = orderService.getOrderByOrderNumber(orderNumber);
        if (!Objects.equals(order.getUserId(), userId)) {
            throw new YamiShopBindException("你没有权限获取该订单信息");
        }
        if (!Objects.equals(order.getStatus(), OrderStatus.CONSIGNMENT.value())) {
            throw new YamiShopBindException("订单未发货，无法确认收货");
        }
        List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderNumber(orderNumber);
        order.setOrderItems(orderItems);
        // 确认收货
        orderService.confirmOrder(Collections.singletonList(order));

        for (OrderItem orderItem : orderItems) {
            productService.removeProductCacheByProdId(orderItem.getProdId());
            skuService.removeSkuCacheBySkuId(orderItem.getSkuId(), orderItem.getProdId());
        }
        return ServerResponseEntity.success();
    }

    /**
     * 删除订单
     */
    @DeleteMapping("/{orderNumber}")
    @Operation(summary = "根据订单号删除订单", description = "根据订单号删除订单")
    @Parameter(name = "orderNumber", description = "订单号", required = true)
    public ServerResponseEntity<String> delete(@PathVariable("orderNumber") String orderNumber) {
        String userId = SecurityUtils.getUser().getUserId();

        Order order = orderService.getOrderByOrderNumber(orderNumber);
        if (order == null) {
            throw new YamiShopBindException("该订单不存在");
        }
        if (!Objects.equals(order.getUserId(), userId)) {
            throw new YamiShopBindException("你没有权限获取该订单信息");
        }
        if (!Objects.equals(order.getStatus(), OrderStatus.SUCCESS.value()) && !Objects.equals(order.getStatus(), OrderStatus.CLOSE.value())) {
            throw new YamiShopBindException("订单未完成或未关闭，无法删除订单");
        }

        // 删除订单
        orderService.deleteOrders(Collections.singletonList(order));

        return ServerResponseEntity.success("删除成功");
    }

    /**
     * 获取我的订单订单数量
     * 数据集成到userInfo接口中
     */
    @Deprecated
    @GetMapping("/orderCount")
    @Operation(summary = "获取我的订单订单数量", description = "获取我的订单订单数量")
    public ServerResponseEntity<OrderCountData> getOrderCount() {
        String userId = SecurityUtils.getUser().getUserId();
        OrderCountData orderCountMap = orderService.getOrderCount(userId);
        return ServerResponseEntity.success(orderCountMap);
    }


}
