

package com.yami.shop.sys.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yami.shop.common.annotation.SysLog;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.PageParam;
import com.yami.shop.sys.model.SysPrinter;
import com.yami.shop.sys.service.SysPrinterService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * 打印机信息管理
 * @author c'p'y
 */
@RestController
@RequestMapping("/sys/printer")
public class SysPrinterController {
	@Autowired
	private SysPrinterService sysPrinterService;

	/**
	 * 所有打印机列表
	 */
	@GetMapping("/page")
	@PreAuthorize("@pms.hasPermission('sys:printer:page')")
	public ServerResponseEntity<IPage<SysPrinter>> page(String remark, PageParam<SysPrinter> page){
		IPage<SysPrinter> sysPrinters = sysPrinterService.page(page, new LambdaQueryWrapper<SysPrinter>().like(StrUtil.isNotBlank(remark), SysPrinter::getRemark, remark));
		return ServerResponseEntity.success(sysPrinters);
	}


	/**
	 * 打印机信息
	 */
	@GetMapping("/info/{id}")
	@PreAuthorize("@pms.hasPermission('sys:printer:info')")
	public ServerResponseEntity<SysPrinter> info(@PathVariable("id") Long id){
		SysPrinter printer = sysPrinterService.getById(id);
		return ServerResponseEntity.success(printer);
	}

	/**
	 * 保存打印机
	 */
	@SysLog("保存打印机")
	@PostMapping
	@PreAuthorize("@pms.hasPermission('sys:printer:save')")
	public ServerResponseEntity<Void> save(@RequestBody @Valid SysPrinter printer){
		sysPrinterService.save(printer);
		return ServerResponseEntity.success();
	}

	/**
	 * 修改打印机
	 */
	@SysLog("修改打印机")
	@PutMapping
	@PreAuthorize("@pms.hasPermission('sys:printer:update')")
	public ServerResponseEntity<Void> update(@RequestBody @Valid SysPrinter printer){
		sysPrinterService.updateById(printer);
		return ServerResponseEntity.success();
	}

	/**
	 * 删除打印机
	 */
	@SysLog("删除打印机")
	@DeleteMapping
	@PreAuthorize("@pms.hasPermission('sys:printer:delete')")
	public ServerResponseEntity<Void> delete(@RequestBody Long[] configIds){
		sysPrinterService.deleteBatch(configIds);
		return ServerResponseEntity.success();
	}

}
