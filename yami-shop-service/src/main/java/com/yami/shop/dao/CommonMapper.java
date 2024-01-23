

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
