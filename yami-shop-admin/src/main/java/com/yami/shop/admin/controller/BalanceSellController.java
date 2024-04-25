package com.yami.shop.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yami.shop.bean.model.UserBalanceSell;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.common.util.QueryUtil;
import com.yami.shop.security.admin.util.SecurityUtils;
import com.yami.shop.service.UserBalanceSellService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 在售储值卡
 *
 * @author c'p'y
 */
@RestController
@RequestMapping("/shop/balanceSell")
public class BalanceSellController {

    @Autowired
    private UserBalanceSellService userBalanceSellService;

    /**
     * 分页获取
     */
    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('shop:balanceSell:page')")
    public ServerResponseEntity<IPage<UserBalanceSell>> page(UserBalanceSell userBalanceSell, PageParam<UserBalanceSell> page) {
        LambdaQueryWrapper<UserBalanceSell> queryWrapper = new LambdaQueryWrapper<UserBalanceSell>().eq(UserBalanceSell::getShopId, SecurityUtils.getSysUser().getShopId()).eq(userBalanceSell.getStatus() != null, UserBalanceSell::getStatus, userBalanceSell.getStatus()).orderByAsc(UserBalanceSell::getSellCnt);
        QueryUtil.pageOrder(page);
        IPage<UserBalanceSell> deliveryUserIPage = userBalanceSellService.page(page, queryWrapper);
        return ServerResponseEntity.success(deliveryUserIPage);
    }

    /**
     * 获取信息
     */
    @GetMapping("/info/{id}")
    public ServerResponseEntity<UserBalanceSell> info(@PathVariable("id") Long id) {
        UserBalanceSell userBalanceSell = userBalanceSellService.getById(id);
        return ServerResponseEntity.success(userBalanceSell);
    }

    /**
     * 保存
     */
    @PostMapping
    @PreAuthorize("@pms.hasPermission('shop:balanceSell:save')")
    public ServerResponseEntity<Void> save(@RequestBody @Valid UserBalanceSell userBalanceSell) {
        userBalanceSell.setShopId(SecurityUtils.getSysUser().getShopId());
        userBalanceSellService.save(userBalanceSell);
        return ServerResponseEntity.success();
    }

    /**
     * 修改
     */
    @PutMapping
    @PreAuthorize("@pms.hasPermission('shop:balanceSell:update')")
    public ServerResponseEntity<Void> update(@RequestBody @Valid UserBalanceSell userBalanceSell) {
        if (userBalanceSell.getShopId() != SecurityUtils.getSysUser().getShopId()) {
            throw new YamiShopBindException("您没有权限操作该信息");
        }
        userBalanceSellService.updateById(userBalanceSell);
        return ServerResponseEntity.success();
    }

    /**
     * 删除
     */
    @DeleteMapping
    @PreAuthorize("@pms.hasPermission('shop:balanceSell:delete')")
    public ServerResponseEntity<Void> delete(@RequestBody List<Long> ids) {
        userBalanceSellService.removeByIds(ids);
        return ServerResponseEntity.success();
    }
}
