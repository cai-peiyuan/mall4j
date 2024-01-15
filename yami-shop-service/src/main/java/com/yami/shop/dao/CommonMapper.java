/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */

package com.yami.shop.dao;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yami.shop.bean.app.dto.MyOrderDto;
import com.yami.shop.bean.app.dto.OrderCountData;
import com.yami.shop.bean.distribution.UserShoppingDataDto;
import com.yami.shop.bean.model.Order;
import com.yami.shop.bean.param.OrderParam;
import com.yami.shop.common.util.PageAdapter;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @date 2024年1月15日
 * @author cai
 */
public interface CommonMapper extends BaseMapper {

    /**
     * 写入打印日志信息
     * @param maps
     */
    void addPrinterLogs(@Param("maps") List<Map> maps);
}
