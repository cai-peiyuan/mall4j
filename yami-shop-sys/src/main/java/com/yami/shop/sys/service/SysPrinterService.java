

package com.yami.shop.sys.service;


import com.baomidou.mybatisplus.extension.service.IService;
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
