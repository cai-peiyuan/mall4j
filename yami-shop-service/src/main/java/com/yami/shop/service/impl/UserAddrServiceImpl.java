package com.yami.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.app.dto.UserAddrDto;
import com.yami.shop.bean.model.Area;
import com.yami.shop.bean.model.UserAddr;
import com.yami.shop.dao.UserAddrMapper;
import com.yami.shop.service.AreaService;
import com.yami.shop.service.UserAddrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lanhai
 */
@Service
public class UserAddrServiceImpl extends ServiceImpl<UserAddrMapper, UserAddr> implements UserAddrService {

    @Autowired
    private UserAddrMapper userAddrMapper;

    @Autowired
    private AreaService areaService;


    /**
     * 根据用户编号获取用户的默认收货地址
     *
     * @param userId
     * @return
     */
    @Override
    public UserAddr getDefaultUserAddr(String userId) {
        return userAddrMapper.getDefaultUserAddr(userId);
    }


    /**
     * 更新用户的默认收货地址
     *
     * @param addrId 默认地址id
     * @param userId 用户id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDefaultUserAddr(Long addrId, String userId) {
        userAddrMapper.removeDefaultUserAddr(userId);
        int setCount = userAddrMapper.setDefaultUserAddr(addrId, userId);
        /*if (setCount == 0) {
            throw new YamiShopBindException("无法修改用户默认地址，请稍后再试");
        }*/
    }


    /**
     * 移除缓存中的用户地址信息
     *
     * @param addrId
     * @param userId
     */
    @Override
    @CacheEvict(cacheNames = "UserAddrDto", key = "#userId+':'+#addrId")
    public void removeUserAddrByUserId(Long addrId, String userId) {

    }

    /**
     * 根据用户的地址编号获取用户的收货地址
     * 如果编号不存在获取用户默认的收货地址
     *
     * @param addrId
     * @param userId
     * @return
     */
    @Override
    @Cacheable(cacheNames = "UserAddrDto", key = "#userId+':'+#addrId")
    public UserAddr getUserAddrByUserId(Long addrId, String userId) {
        if (addrId == 0) {
            return userAddrMapper.getDefaultUserAddr(userId);
        } else {
            return userAddrMapper.getUserAddrByUserIdAndAddrId(userId, addrId);
        }
    }

    /**
     * 判断用户选择的收货地址是否支持配送
     *
     * @param userAddr
     * @return
     */
    @Override
    public boolean validUserAddr(UserAddrDto userAddr) {
        List<Area> areas = areaService.listValid();
        List<Area> provinceValid = areas.stream().filter(area -> area.getAreaName().equals(userAddr.getProvince())).collect(Collectors.toList());
        List<Area> cityValid = areas.stream().filter(area -> area.getAreaName().equals(userAddr.getCity())).collect(Collectors.toList());
        List<Area> areaValid = areas.stream().filter(area -> area.getAreaName().equals(userAddr.getArea())).collect(Collectors.toList());

        return provinceValid != null && provinceValid.size() > 0 && cityValid != null && cityValid.size() > 0 && areaValid != null && areaValid.size() > 0;
    }


}
