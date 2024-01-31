package com.yami.shop.admin.listener;

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
 */
@Slf4j
public class WebApplicationStartedListener implements ApplicationListener<ApplicationStartedEvent> {
    /**
     * @param event
     * @author peiyuan.cai
     * @date 2024/1/23 13:26 星期二
     */
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        log.debug("web管理端接口服务启动完成");
        RedisTemplate<String, Object> redisTemplate = SpringContextUtils.getBean("redisTemplate", RedisTemplate.class);
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(KEY_SYS_CONFIG);
        WeChatPayUtil.setValues(entries);
        log.debug("初始化系统参数 {}", entries.size());
        WeChatPayUtil.initConfig();
        log.debug("初始化微信支付");
    }
}
