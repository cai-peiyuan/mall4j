

package com.yami.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.shop.bean.app.dto.DeliveryDto;
import com.yami.shop.bean.model.Delivery;

/**
 *
 * @author lgh on 2018/11/26.
 */
public interface DeliveryService extends IService<Delivery> {

    DeliveryDto getDeliveryInfoByOrderNumber(String orderNumber);

    DeliveryDto getDeliveryInfoByExpressNumber(String expressNumber);
}
