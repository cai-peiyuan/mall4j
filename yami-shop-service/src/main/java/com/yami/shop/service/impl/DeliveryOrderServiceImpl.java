

package com.yami.shop.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.bean.model.DeliveryOrder;
import com.yami.shop.dao.DeliveryOrderMapper;
import com.yami.shop.service.DeliveryOrderService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 *
 * @author c'p'y
 */
@Service
public class DeliveryOrderServiceImpl extends ServiceImpl<DeliveryOrderMapper, DeliveryOrder> implements DeliveryOrderService {

    /**
     * 生成物流运单号码
     * DS 为袋鼠的前缀
     * 12位日期 时分秒
     * 4位随机数字
     * @return
     */
    @Override
    public String generateExpressNumber() {
        String expressNumber = "DS"+ DateUtil.format(new Date(),"yyyyMMddHHmmss") + RandomUtil.randomNumbers(4);
        return expressNumber;
    }
}
