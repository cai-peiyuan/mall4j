/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */

package com.yami.shop.sys.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yami.shop.sys.model.SysPrinter;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统打印机
 * @author c'p'y
 */
public interface SysPrinterMapper extends BaseMapper<SysPrinter>  {

	/**
	 * 根据key，查询系统配置信息
	 * @param key key
	 * @return SysPrinter
	 */
	SysPrinter queryByKey(String key);

	/**
	 * 根据key，更新value
	 * @param key
	 * @param value
	 * @return 更新成功条数
	 */
	int updateValueByKey(@Param("key") String key, @Param("value") String value);

	/**
	 * 根据key查询系统配置信息
	 * @param key
	 * @return 系统配置信息列表
	 */
	List<SysPrinter> selectByKeyLike(String key);

	/**
	 * 批量删除系统配置
	 * @param ids 系统配置信息数组
	 */
	void deleteBatch(@Param("ids") Long[] ids);
	
}
