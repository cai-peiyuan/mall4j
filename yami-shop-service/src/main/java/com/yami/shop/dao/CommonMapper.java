

package com.yami.shop.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

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
