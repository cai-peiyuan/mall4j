

package com.yami.shop.service;

import java.util.Date;
import java.util.Map;

/**
 * @author peiyuan.cai
 * @date 2024/4/25 12:03 星期四
 */
public interface AnalysisService {

    Map<String, Object> indexAnalysisData(Long shopId, Date startTime, Date endTime);
}
