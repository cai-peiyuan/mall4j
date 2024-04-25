
package com.yami.shop.admin.controller;

import cn.hutool.core.date.DateUtil;
import com.jfinal.kit.StrKit;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.security.admin.util.SecurityUtils;
import com.yami.shop.service.AnalysisService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 主页 数据交互接口
 *
 * @author peiyuan.cai
 * @date 2024/4/25 12:00 星期四
 */
@RestController
@RequestMapping("/home")
@Tag(name = "主页")
@AllArgsConstructor
public class HomeController {

    private final AnalysisService analysisService;

    @GetMapping("/analysisData")
    @Parameter(name = "startTime", description = "开始时间", required = false)
    @Parameter(name = "endTime", description = "结束时间", required = false)
    public ServerResponseEntity<Map<String, Object>> analysisData(@RequestParam(value = "startTime") String startTime, @RequestParam(value = "endTime") String endTime) {
        Long shopId = SecurityUtils.getSysUser().getShopId();

        /**
         * 默认当日0点至第二日0点
         * 一整天的数据
         */
        Date date = new Date();
        Date start = DateUtil.beginOfDay(date);
        if (StrKit.notBlank(startTime)) {
            start = DateUtil.parseDate(startTime);
        }

        Date end = DateUtil.offsetDay(start, 1);
        if (StrKit.notBlank(endTime)) {
            end = DateUtil.parseDate(endTime);
        }

        Map<String, Object> data = analysisService.indexAnalysisData(shopId, start, end);
        return ServerResponseEntity.success(data);
    }
}
