package com.yami.shop.security.common.adapter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * @author 菠萝凤梨
 * @date 2022/3/25 17:33
 */
@Slf4j
public class DefaultAuthConfigAdapter implements AuthConfigAdapter {

    public DefaultAuthConfigAdapter() {
        log.info("not implement other AuthConfigAdapter, use DefaultAuthConfigAdapter... all url need auth...");
    }

    @Override
    public List<String> pathPatterns() {
        return Collections.singletonList("/*");
    }

    @Override
    public List<String> excludePathPatterns() {
        return Collections.emptyList();
    }
}
