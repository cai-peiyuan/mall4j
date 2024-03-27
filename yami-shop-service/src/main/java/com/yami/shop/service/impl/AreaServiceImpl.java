package com.yami.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.model.Area;
import com.yami.shop.dao.AreaMapper;
import com.yami.shop.service.AreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lgh on 2018/10/26.
 */
@Service
public class AreaServiceImpl extends ServiceImpl<AreaMapper, Area> implements AreaService {

    @Autowired
    private AreaMapper areaMapper;

    /**
     * 获取所有有效的地区并且缓存
     *
     * @return
     */
    @Override
    @Cacheable(cacheNames = "area", key = "valids")
    public List<Area> listValid() {
        // 只查询有效的地址行政区 status == 1
        return areaMapper.selectList(new LambdaQueryWrapper<Area>().eq(Area::getStatus, 1));
    }

    /**
     * 清除地址缓存
     */
    @Override
    @CacheEvict(cacheNames = "area", key = "valids")
    public void removeAreaValidCache() {

    }

    @Override
    @Cacheable(cacheNames = "area", key = "#pid")
    public List<Area> listByPid(Long pid) {
        // 只查询有效的地址行政区 status == 1
        return areaMapper.selectList(new LambdaQueryWrapper<Area>().eq(Area::getParentId, pid).eq(Area::getStatus, 1));
    }

    @Override
    @CacheEvict(cacheNames = "area", key = "#pid")
    public void removeAreaCacheByParentId(Long pid) {

    }
}
