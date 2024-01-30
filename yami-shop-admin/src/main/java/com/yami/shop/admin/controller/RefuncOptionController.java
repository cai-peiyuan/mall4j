package com.yami.shop.admin.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yami.shop.bean.model.RefundOption;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.security.admin.util.SecurityUtils;
import com.yami.shop.service.RefundOptionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 退款选项
 *
 * @author c'p'y
 */
@RestController
@RequestMapping("/shop/refundOption")
public class RefuncOptionController {

    @Autowired
    private RefundOptionService refundOptionService;

    /**
     * 分页获取
     */
    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('shop:refundOption:page')")
    public ServerResponseEntity<IPage<RefundOption>> page(RefundOption refundOption, PageParam<RefundOption> page) {
        IPage<RefundOption> deliveryUserIPage = refundOptionService.page(page, new LambdaQueryWrapper<RefundOption>()
                .eq(RefundOption::getShopId, SecurityUtils.getSysUser().getShopId())
                .like(StrUtil.isNotBlank(refundOption.getRefundName()), RefundOption::getRefundName, refundOption.getRefundName())
                .eq(refundOption.getStatus() != null, RefundOption::getStatus, refundOption.getStatus()).orderByAsc(RefundOption::getSeq));
        return ServerResponseEntity.success(deliveryUserIPage);
    }

    /**
     * 获取信息
     */
    @GetMapping("/info/{id}")
    public ServerResponseEntity<RefundOption> info(@PathVariable("id") Long id) {
        RefundOption refundOption = refundOptionService.getById(id);
        return ServerResponseEntity.success(refundOption);
    }

    /**
     * 保存
     */
    @PostMapping
    @PreAuthorize("@pms.hasPermission('shop:refundOption:save')")
    public ServerResponseEntity<Void> save(@RequestBody @Valid RefundOption refundOption) {
        refundOption.setShopId(SecurityUtils.getSysUser().getShopId());
        refundOptionService.save(refundOption);
        return ServerResponseEntity.success();
    }

    /**
     * 修改
     */
    @PutMapping
    @PreAuthorize("@pms.hasPermission('shop:refundOption:update')")
    public ServerResponseEntity<Void> update(@RequestBody @Valid RefundOption refundOption) {
        refundOptionService.updateById(refundOption);
        return ServerResponseEntity.success();
    }

    /**
     * 删除
     */
    @DeleteMapping
    @PreAuthorize("@pms.hasPermission('shop:refundOption:delete')")
    public ServerResponseEntity<Void> delete(@RequestBody List<Long> ids) {
        refundOptionService.removeByIds(ids);
        return ServerResponseEntity.success();
    }
}
