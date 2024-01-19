/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */

package com.yami.shop.sys.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.shop.sys.model.SysConfig;

/**
 * 系统配置信息
 * @author lgh
 */
public interface SysConfigService extends IService<SysConfig>  {

	/**
	 * 根据key，更新value
	 * @param key 参数key
	 * @param value 参数value
	 */
	void updateValueByKey(String key, String value);

	/**
	 * 根据key，更新remark
	 * @param key 参数key
	 * @param remark 参数value
	 */
	void updateRemarkByKey(String key, String remark);

	/**
	 * 删除配置信息
	 * @param ids 配置项id列表
	 */
	void deleteBatch(Long[] ids);

	/**
	 * 根据key，获取配置的value值
	 * @param key 参数key
	 * @return value
	 */
	String getValue(String key);

}
