package com.yami.shop.api.listener;

import com.qq.wechat.pay.WeChatPayUtil;
import com.yami.shop.common.util.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;

import static com.yami.shop.common.constants.Constant.KEY_SYS_CONFIG;

/**
 * @author peiyuan.cai
 * @date 2024/1/23 13:26 星期二
 */
@Slf4j
public class ApiApplicationStartedListener implements ApplicationListener<ApplicationStartedEvent> {
    /**
     * @param event
     * @author peiyuan.cai
     * @date 2024/1/23 13:26 星期二
     */
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        log.debug("Api接口服务启动完成");
        RedisTemplate<String, Object> redisTemplate = SpringContextUtils.getBean("redisTemplate", RedisTemplate.class);
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(KEY_SYS_CONFIG);
        log.debug("初始化系统参数 {}", entries.size());
        WeChatPayUtil.setValues(entries);
        WeChatPayUtil.initConfig();
    }
}
