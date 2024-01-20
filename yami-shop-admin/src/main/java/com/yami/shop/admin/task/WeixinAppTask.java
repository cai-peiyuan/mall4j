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

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.jfinal.kit.HttpKit;
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

import java.util.HashMap;
import java.util.List;


/**
 * 微信小程序相关定时任务
 *
 * @author cpy
 * @see com.yami.shop.admin.config.XxlJobConfig
 */
@Component("weixinAppTask")
public class WeixinAppTask {

    private static final Logger logger = LoggerFactory.getLogger(WeixinAppTask.class);

    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取小程序accessToken信息
     * 小于2小时执行一次
     * @link https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/mp-access-token/getAccessToken.html
     * @see
     */
    @XxlJob("getAccessToken")
    public void getAccessToken() {
        logger.debug("获取小程序accessToken信息");
        String url = "https://api.weixin.qq.com/cgi-bin/token";
        String appid = sysConfigService.getValue("wxapp_appId");
        String secret = sysConfigService.getValue("wxapp_secret");

        // secret = (String) redisTemplate.opsForHash().get("sys:config", "wxapp_secret");
        // appid = (String) redisTemplate.opsForHash().get("sys:config", "wxapp_appId");
        String getAccessTokenResult = HttpUtil.get(url, new HashMap<>() {{
            put("grant_type", "client_credential");
            put("appid", appid);
            put("secret", secret);
        }});
        /**
         * {
         * "access_token":"ACCESS_TOKEN",
         * "expires_in":7200
         * }
         */
        logger.debug("获取微信access_token数据："+getAccessTokenResult);
        JSONObject jsonObject = JSON.parseObject(getAccessTokenResult);
        String access_token = jsonObject.getString("access_token");
        int expires_in = jsonObject.getInteger("expires_in");

        sysConfigService.updateValueByKey("wxapp_access_token", access_token);
        sysConfigService.updateRemarkByKey("wxapp_access_token", getAccessTokenResult);
        //放入redis缓存
        redisTemplate.opsForHash().put("sys:config", "wxapp_access_token", access_token);
    }


}
