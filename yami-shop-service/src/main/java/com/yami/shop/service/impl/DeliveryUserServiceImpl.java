

package com.yami.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.dto.HotSearchDto;
import com.yami.shop.bean.model.DeliveryUser;
import com.yami.shop.bean.model.HotSearch;
import com.yami.shop.dao.DeliveryUserMapper;
import com.yami.shop.dao.HotSearchMapper;
import com.yami.shop.service.DeliveryUserService;
import com.yami.shop.service.HotSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 配送人员
 */
@Service
public class DeliveryUserServiceImpl extends ServiceImpl<DeliveryUserMapper, DeliveryUser> implements DeliveryUserService {

}
