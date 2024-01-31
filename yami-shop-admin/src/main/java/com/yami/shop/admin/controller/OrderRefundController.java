package com.yami.shop.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.base.Objects;
import com.yami.shop.bean.model.OrderRefund;
import com.yami.shop.bean.param.OrderRefundQueryParam;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.security.admin.util.SecurityUtils;
import com.yami.shop.service.OrderRefundService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单退款
 *
 * @author c'p'y
 */
@Slf4j
@RestController
@RequestMapping("/order/refund")
public class OrderRefundController {

    @Autowired
    private OrderRefundService orderRefundService;

    /**
     * 分页获取订单退款信息
     *
     * @author cpy
     */
    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('order:refund:page')")
    public ServerResponseEntity<IPage<OrderRefund>> page(OrderRefundQueryParam queryParam, PageParam<OrderRefund> page) {
        Long shopId = SecurityUtils.getSysUser().getShopId();
        IPage<OrderRefund> pageData = orderRefundService.page(page,
                new LambdaQueryWrapper<OrderRefund>()
                        .eq(OrderRefund::getShopId, shopId)
                        .like(queryParam.getOrderNumber() != null, OrderRefund::getOrderNumber, queryParam.getOrderNumber())
                        .like(queryParam.getApplyType() != null, OrderRefund::getApplyType, queryParam.getApplyType())
                        .orderByDesc(OrderRefund::getApplyTime));
        return ServerResponseEntity.success(pageData);
    }

    /**
     * 针对退款订单执行退款操作 发送到腾讯支付平台的退款
     *
     * @author cpy
     */
    @GetMapping("/accept/{refundId}")
    @PreAuthorize("@pms.hasPermission('order:refund:accept')")
    public ServerResponseEntity<Object> refundAccept(@PathVariable(name = "refundId") String refundId) {
        Long shopId = SecurityUtils.getSysUser().getShopId();
        OrderRefund refund = orderRefundService.getById(refundId);
        if (!Objects.equal(shopId, refund.getShopId())) {
            throw new YamiShopBindException("您没有权限操作该订单信息");
        }
        orderRefundService.refundAccept(refund);
        return ServerResponseEntity.success();
    }

    /**
     * 获取订单退款详细信息
     *
     * @author cpy
     */
    @GetMapping("/orderRefundInfo/{orderNumber}")
    @PreAuthorize("@pms.hasPermission('order:refund:info')")
    public ServerResponseEntity<OrderRefund> info(@PathVariable("orderNumber") String orderNumber) {
        Long shopId = SecurityUtils.getSysUser().getShopId();
        OrderRefund orderRefund = orderRefundService.getOne(new LambdaQueryWrapper<OrderRefund>()
                // .eq(OrderRefund::getShopId, shopId)
                .eq(OrderRefund::getOrderNumber, orderNumber)
        );
        if (!Objects.equal(shopId, orderRefund.getShopId())) {
            throw new YamiShopBindException("您没有权限获取该订单信息");
        }
        return ServerResponseEntity.success(orderRefund);
    }

}
