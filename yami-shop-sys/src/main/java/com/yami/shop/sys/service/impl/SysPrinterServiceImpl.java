

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
