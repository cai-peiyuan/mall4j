

package com.yami.shop.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.model.DeliveryOrder;
import com.yami.shop.bean.model.DeliveryOrderRoute;
import com.yami.shop.dao.DeliveryOrderMapper;
import com.yami.shop.dao.DeliveryOrderRouteMapper;
import com.yami.shop.service.DeliveryOrderRouteService;
import com.yami.shop.service.DeliveryOrderService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 *
 * @author c'p'y
 */
@Service
public class DeliveryOrderRouteServiceImpl extends ServiceImpl<DeliveryOrderRouteMapper, DeliveryOrderRoute> implements DeliveryOrderRouteService {

}
