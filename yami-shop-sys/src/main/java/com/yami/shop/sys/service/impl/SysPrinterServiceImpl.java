/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */

package com.yami.shop.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yami.shop.sys.dao.SysPrinterMapper;
import com.yami.shop.sys.model.SysPrinter;
import com.yami.shop.sys.service.SysPrinterService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author lgh
 */
@Service("sysPrinterService")
@AllArgsConstructor
public class SysPrinterServiceImpl extends ServiceImpl<SysPrinterMapper, SysPrinter> implements SysPrinterService {

	private final SysPrinterMapper sysPrinterMapper;
	
	@Override
	public void deleteBatch(Long[] ids) {
		sysPrinterMapper.deleteBatch(ids);
	}

}
