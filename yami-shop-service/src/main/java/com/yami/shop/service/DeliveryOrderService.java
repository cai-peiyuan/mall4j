

package com.yami.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.shop.bean.model.Delivery;
import com.yami.shop.bean.model.DeliveryOrder;

/**
 *
 * @author c'p'y
 */
public interface DeliveryOrderService extends IService<DeliveryOrder> {

    /**
     * 生成物流运单号码
     * @return
     */
    String generateExpressNumber();
}
