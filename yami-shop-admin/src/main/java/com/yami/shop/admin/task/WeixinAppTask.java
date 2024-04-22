

package com.yami.shop.admin.task;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.yami.shop.sys.service.SysConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;

import static com.yami.shop.common.constants.Constant.KEY_SYS_CONFIG;


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

        // secret = (String) redisTemplate.opsForHash().get(KEY_SYS_CONFIG, "wxapp_secret");
        // appid = (String) redisTemplate.opsForHash().get(KEY_SYS_CONFIG, "wxapp_appId");
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
        redisTemplate.opsForHash().put(KEY_SYS_CONFIG, "wxapp_access_token", access_token);
    }


}
