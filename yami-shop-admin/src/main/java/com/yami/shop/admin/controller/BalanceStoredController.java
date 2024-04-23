package com.yami.shop.admin.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yami.shop.bean.model.UserBalanceStored;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.security.admin.util.SecurityUtils;
import com.yami.shop.service.UserBalanceSellService;
import com.yami.shop.service.UserBalanceStoredService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 充值卡
 *
 * @author c'p'y
 */
@RestController
@RequestMapping("/shop/balanceStored")
public class BalanceStoredController {

    @Autowired
    private UserBalanceStoredService userBalanceStoredService;

    /**
     * 分页获取
     */
    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('shop:balanceStored:page')")
    public ServerResponseEntity<IPage<UserBalanceStored>> page(UserBalanceStored userBalanceStored, PageParam<UserBalanceStored> page) {
        LambdaQueryWrapper<UserBalanceStored> queryWrapper = new LambdaQueryWrapper<UserBalanceStored>().eq(UserBalanceStored::getShopId, SecurityUtils.getSysUser().getShopId()).eq(StrUtil.isNotBlank(userBalanceStored.getStatus()), UserBalanceStored::getStatus, userBalanceStored.getStatus()).orderByDesc(UserBalanceStored::getCreateTime);
        IPage<UserBalanceStored> deliveryUserIPage = userBalanceStoredService.page(page, queryWrapper);
        return ServerResponseEntity.success(deliveryUserIPage);
    }

    /**
     * 获取信息
     */
    @GetMapping("/info/{id}")
    public ServerResponseEntity<UserBalanceStored> info(@PathVariable("id") Long id) {
        UserBalanceStored userBalanceStored = userBalanceStoredService.getById(id);
        return ServerResponseEntity.success(userBalanceStored);
    }

    /**
     * 保存
     */
    @PostMapping
    @PreAuthorize("@pms.hasPermission('shop:balanceStored:save')")
    public ServerResponseEntity<Void> save(@RequestBody @Valid UserBalanceStored userBalanceStored) {
        userBalanceStored.setShopId(SecurityUtils.getSysUser().getShopId());
        userBalanceStoredService.save(userBalanceStored);
        return ServerResponseEntity.success();
    }

    /**
     * 修改
     */
    @PutMapping
    @PreAuthorize("@pms.hasPermission('shop:balanceStored:update')")
    public ServerResponseEntity<Void> update(@RequestBody @Valid UserBalanceStored userBalanceStored) {
        userBalanceStoredService.updateById(userBalanceStored);
        return ServerResponseEntity.success();
    }

    /**
     * 删除
     */
    @DeleteMapping
    @PreAuthorize("@pms.hasPermission('shop:balanceStored:delete')")
    public ServerResponseEntity<Void> delete(@RequestBody List<Long> ids) {
        userBalanceStoredService.removeByIds(ids);
        return ServerResponseEntity.success();
    }
}
