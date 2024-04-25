

package com.yami.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.shop.bean.model.Area;

import java.util.List;

/**
 * @author lgh on 2018/10/26.
 */
public interface AreaService extends IService<Area> {

    /**
     * 获取所有有效的地区并且缓存
     *
     * @return
     */
    List<Area> listAreasByStatus(Integer status);


    /**
     * 清除地址缓存
     *
     */
    void removeAreasByStatusCache(Integer status);

    /**
     * 通过pid 查找地址接口
     *
     * @param pid 父id
     * @return
     */
    List<Area> listByPid(Long pid);

    /**
     * 通过pid 清除地址缓存
     *
     * @param pid
     */
    void removeAreaCacheByParentId(Long pid);

}
