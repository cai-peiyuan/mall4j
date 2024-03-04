package com.yami.shop.admin.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yami.shop.bean.model.UserBalanceOrder;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.service.UserBalanceOrderService;
import com.yami.shop.service.UserService;
import com.yami.shop.service.WxShipInfoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * 后台管理接口
 * 充值订单查询
 * @author lgh
 */
@RestController
@RequestMapping("/admin/balance/order")
public class UserBalanceOrderController {

    @Autowired
    private UserBalanceOrderService userBalanceOrderService;

    @Autowired
    private UserService userService;

    @Autowired
    private WxShipInfoService wxShipInfoService;

    /**
     * 分页获取
     */
    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('order:balance:page')")
    public ServerResponseEntity<IPage<UserBalanceOrder>> page(UserBalanceOrder balanceOrder, PageParam<UserBalanceOrder> page) {
        IPage<UserBalanceOrder> brands = userBalanceOrderService.page(page,
                new LambdaQueryWrapper<UserBalanceOrder>()
                        .like(StrUtil.isNotBlank(balanceOrder.getUserId()), UserBalanceOrder::getUserId, balanceOrder.getUserId())
                        .like(StrUtil.isNotBlank(balanceOrder.getOrderNumber()), UserBalanceOrder::getOrderNumber, balanceOrder.getOrderNumber())
                        .orderByDesc(UserBalanceOrder::getCreateTime));

        brands.getRecords().forEach(record -> {
            record.setUser(userService.getUserByUserId(record.getUserId()));
        });

        return ServerResponseEntity.success(brands);
    }

    /**
     * 获取信息
     */
    @GetMapping("/info/{id}")
    @PreAuthorize("@pms.hasPermission('order:balance:info')")
    public ServerResponseEntity<UserBalanceOrder> info(@PathVariable("id") Long id) {
        UserBalanceOrder balanceOrder = userBalanceOrderService.getById(id);
        return ServerResponseEntity.success(balanceOrder);
    }

    /**
     * 保存
     */
    @PostMapping
    @PreAuthorize("@pms.hasPermission('order:balance:save')")
    public ServerResponseEntity<Void> save(@Valid UserBalanceOrder balanceOrder) {
        userBalanceOrderService.save(balanceOrder);
        return ServerResponseEntity.success();
    }

    /**
     * 修改
     */
    @PutMapping
    @PreAuthorize("@pms.hasPermission('order:balance:update')")
    public ServerResponseEntity<Void> update(@Valid UserBalanceOrder balanceOrder) {
        userBalanceOrderService.updateById(balanceOrder);
        return ServerResponseEntity.success();
    }

    /**
     * 手动发货
     */
    @GetMapping("/sendDeliveryInfo/{orderNumber}")
    @PreAuthorize("@pms.hasPermission('order:balance:delivery')")
    public ServerResponseEntity<Void> sendDeliveryInfo(@PathVariable(name = "orderNumber") String orderNumber) {
        wxShipInfoService.uploadBalanceOrderShip(orderNumber, null);
        return ServerResponseEntity.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('order:balance:delete')")
    public ServerResponseEntity<Void> delete(@PathVariable Long id) {
        userBalanceOrderService.removeById(id);
        return ServerResponseEntity.success();
    }

}
