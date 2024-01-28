package com.yami.shop.admin.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yami.shop.bean.model.DeliveryUser;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.security.admin.util.SecurityUtils;
import com.yami.shop.service.DeliveryUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 配送人员管理
 *
 * @author c'p'y
 */
@RestController
@RequestMapping("/shop/deliveryUser")
public class DeliveryUserController {

    @Autowired
    private DeliveryUserService deliveryUserService;

    /**
     * 分页获取
     */
    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('shop:deliveryUser:page')")
    public ServerResponseEntity<IPage<DeliveryUser>> page(DeliveryUser deliveryUser, PageParam<DeliveryUser> page) {
        IPage<DeliveryUser> deliveryUserIPage = deliveryUserService.page(page, new LambdaQueryWrapper<DeliveryUser>().eq(DeliveryUser::getShopId, SecurityUtils.getSysUser().getShopId()).like(StrUtil.isNotBlank(deliveryUser.getUserName()), DeliveryUser::getUserName, deliveryUser.getUserName()).like(StrUtil.isNotBlank(deliveryUser.getUserPhone()), DeliveryUser::getUserPhone, deliveryUser.getUserPhone()).eq(deliveryUser.getStatus() != null, DeliveryUser::getStatus, deliveryUser.getStatus()).orderByAsc(DeliveryUser::getSeq));
        return ServerResponseEntity.success(deliveryUserIPage);
    }

    /**
     * 获取信息
     */
    @GetMapping("/info/{id}")
    public ServerResponseEntity<DeliveryUser> info(@PathVariable("id") Long id) {
        DeliveryUser deliveryUser = deliveryUserService.getById(id);
        return ServerResponseEntity.success(deliveryUser);
    }

    /**
     * 保存
     */
    @PostMapping
    @PreAuthorize("@pms.hasPermission('shop:deliveryUser:save')")
    public ServerResponseEntity<Void> save(@RequestBody @Valid DeliveryUser deliveryUser) {
        deliveryUser.setShopId(SecurityUtils.getSysUser().getShopId());
        deliveryUserService.save(deliveryUser);
        return ServerResponseEntity.success();
    }

    /**
     * 修改
     */
    @PutMapping
    @PreAuthorize("@pms.hasPermission('shop:deliveryUser:update')")
    public ServerResponseEntity<Void> update(@RequestBody @Valid DeliveryUser deliveryUser) {
        deliveryUserService.updateById(deliveryUser);
        return ServerResponseEntity.success();
    }

    /**
     * 删除
     */
    @DeleteMapping
    @PreAuthorize("@pms.hasPermission('shop:deliveryUser:delete')")
    public ServerResponseEntity<Void> delete(@RequestBody List<Long> ids) {
        deliveryUserService.removeByIds(ids);
        return ServerResponseEntity.success();
    }
}
