/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */

package com.yami.shop.admin.task;

import com.alibaba.fastjson2.JSON;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.yami.shop.sys.model.SysConfig;
import com.yami.shop.sys.model.SysPrinter;
import com.yami.shop.sys.service.SysConfigService;
import com.yami.shop.sys.service.SysPrinterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * 系统缓存定时任务
 *
 * @author cpy
 * @see com.yami.shop.admin.config.XxlJobConfig
 */
@Component("sysCacheTask")
public class SysCacheTask {

    private static final Logger logger = LoggerFactory.getLogger(SysCacheTask.class);

    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private SysPrinterService sysPrinterService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 缓存系统配置参数信息
     */
    @XxlJob("cacheSysConfig")
    public void cacheSysConfig() {
        logger.debug("缓存系统参数");
        List<SysConfig> list = sysConfigService.list();
        for (SysConfig sysConfig : list) {
            redisTemplate.opsForHash().put("sys:config", sysConfig.getParamKey(), sysConfig.getParamValue());
        }
    }


    /**
     * 缓存系统打印机信息
     */
    @XxlJob("cacheSysPrinter")
    public void cacheSysPrinter() {
        logger.debug("缓存系统打印机");
        List<SysPrinter> list = sysPrinterService.list();
        for (SysPrinter sysPrinter : list) {
            redisTemplate.opsForHash().put("sys:printer", sysPrinter.getMachineCode(), JSON.toJSONString(sysPrinter));
        }
    }
}
