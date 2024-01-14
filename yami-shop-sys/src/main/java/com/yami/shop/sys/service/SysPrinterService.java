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
import com.yami.shop.sys.model.SysPrinter;

/**
 * 打印机管理
 * @author c'p'y
 */
public interface SysPrinterService extends IService<SysPrinter>  {

	
	/**
	 * 删除配置信息
	 * @param ids 配置项id列表
	 */
	public void deleteBatch(Long[] ids);
}
