

package com.yami.shop.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jfinal.kit.StrKit;
import com.yami.shop.bean.enums.AreaLevelEnum;
import com.yami.shop.bean.model.Area;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.common.util.QueryUtil;
import com.yami.shop.service.AreaService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * @author lgh on 2018/10/26.
 */
@RestController
@RequestMapping("/admin/area")
@AllArgsConstructor
public class AreaController {

    private final AreaService areaService;

    /**
     * 分页获取
     */
    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('admin:area:page')")
    public ServerResponseEntity<IPage<Area>> page(Area area, PageParam<Area> page) {
        LambdaQueryWrapper<Area> areaLambdaQueryWrapper = new LambdaQueryWrapper<>();
        areaLambdaQueryWrapper
                .like(StrKit.notBlank(area.getAreaName()), Area::getAreaName, area.getAreaName())
                .eq(area.getStatus() != null, Area::getStatus, area.getStatus())
        ;
        QueryUtil.pageOrder(page);
        IPage<Area> sysUserPage = areaService.page(page, areaLambdaQueryWrapper);
        return ServerResponseEntity.success(sysUserPage);
    }

    /**
     * 获取省市
     */
    @GetMapping("/list")
    @PreAuthorize("@pms.hasPermission('admin:area:list')")
    public ServerResponseEntity<List<Area>> list(Area area) {
        List<Area> areas = areaService.list(new LambdaQueryWrapper<Area>()
                .like(area.getAreaName() != null, Area::getAreaName, area.getAreaName()));
        return ServerResponseEntity.success(areas);
    }

    /**
     * 通过父级id获取区域列表
     */
    @GetMapping("/listByPid")
    public ServerResponseEntity<List<Area>> listByPid(Long pid) {
        List<Area> list = areaService.listByPid(pid);
        return ServerResponseEntity.success(list);
    }

    /**
     * 获取信息
     */
    @GetMapping("/info/{id}")
    @PreAuthorize("@pms.hasPermission('admin:area:info')")
    public ServerResponseEntity<Area> info(@PathVariable("id") Long id) {
        Area area = areaService.getById(id);
        return ServerResponseEntity.success(area);
    }

    /**
     * 保存
     */
    @PostMapping
    @PreAuthorize("@pms.hasPermission('admin:area:save')")
    public ServerResponseEntity<Void> save(@Valid @RequestBody Area area) {
        if (area.getParentId() != null) {
            Area parentArea = areaService.getById(area.getParentId());
            area.setLevel(parentArea.getLevel() + 1);
            areaService.removeAreaCacheByParentId(area.getParentId());
        }
        areaService.save(area);
        //移除缓存
        areaService.removeAreasByStatusCache(1);
        return ServerResponseEntity.success();
    }

    /**
     * 修改
     */
    @PutMapping
    @PreAuthorize("@pms.hasPermission('admin:area:update')")
    public ServerResponseEntity<Void> update(@Valid @RequestBody Area area) {
        Area areaDb = areaService.getById(area.getAreaId());
        // 判断当前省市区级别，如果是1级、2级则不能修改级别，不能修改成别人的下级
        if (Objects.equals(areaDb.getLevel(), AreaLevelEnum.FIRST_LEVEL.value()) && !Objects.equals(area.getLevel(), AreaLevelEnum.FIRST_LEVEL.value())) {
            throw new YamiShopBindException("不能改变一级行政地区的级别");
        }
        if (Objects.equals(areaDb.getLevel(), AreaLevelEnum.SECOND_LEVEL.value()) && !Objects.equals(area.getLevel(), AreaLevelEnum.SECOND_LEVEL.value())) {
            throw new YamiShopBindException("不能改变二级行政地区的级别");
        }
        hasSameName(area);
        areaService.updateById(area);
        areaService.removeAreaCacheByParentId(area.getParentId());
        //移除缓存
        areaService.removeAreasByStatusCache(1);
        return ServerResponseEntity.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('admin:area:delete')")
    public ServerResponseEntity<Void> delete(@PathVariable Long id) {
        Area area = areaService.getById(id);
        areaService.removeById(id);
        areaService.removeAreaCacheByParentId(area.getParentId());
        //移除缓存
        areaService.removeAreasByStatusCache(1);
        return ServerResponseEntity.success();
    }

    private void hasSameName(Area area) {
        long count = areaService.count(new LambdaQueryWrapper<Area>()
                .eq(Area::getParentId, area.getParentId())
                .eq(Area::getAreaName, area.getAreaName())
                .ne(Objects.nonNull(area.getAreaId()) && !Objects.equals(area.getAreaId(), 0L), Area::getAreaId, area.getAreaId())
        );
        if (count > 0) {
            throw new YamiShopBindException("该地区已存在");
        }
    }
}
