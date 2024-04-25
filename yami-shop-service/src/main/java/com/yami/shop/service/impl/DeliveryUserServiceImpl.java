

package com.yami.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.model.DeliveryUser;
import com.yami.shop.dao.DeliveryUserMapper;
import com.yami.shop.service.DeliveryUserService;
import org.springframework.stereotype.Service;

/**
 * 配送人员
 */
@Service
public class DeliveryUserServiceImpl extends ServiceImpl<DeliveryUserMapper, DeliveryUser> implements DeliveryUserService {

}
