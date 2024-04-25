

package com.yami.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yami.shop.dao.AnalysisMapper;
import com.yami.shop.service.AnalysisService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author peiyuan.cai
 * @date 2024/4/25 12:04 星期四
 */
@Service
@AllArgsConstructor
public class AnalysisServiceImpl implements AnalysisService {


    private final AnalysisMapper analysisMapper;

    /**
     * @return
     * @author peiyuan.cai
     * @date 2024/4/25 12:05 星期四
     */
    @Override
    public Map<String, Object> indexAnalysisData(Long shopId, Date startTime, Date endTime) {
        Map<String, Object> data = new HashMap<>();
        //储值统计
        JSONObject balance = new JSONObject();
        //订单统计
        JSONObject order = new JSONObject();
        //用户统计
        JSONObject user = new JSONObject();

        /**
         * 余额统计
         */
        Double balanceSum = analysisMapper.queryBalanceSum();
        balance.put("balanceSum", balanceSum);
        //余额大于0的用户数据
        Map hasBalance = analysisMapper.queryBalanceCountAndSumByValue(0.01D, Double.MAX_VALUE);
        balance.put("hasBalance", hasBalance);
        //消费
        Map usedBalanceDetailByDate = analysisMapper.queryUsedBalanceDetailByDate(startTime, endTime);
        balance.put("usedBalanceDetailByDate", usedBalanceDetailByDate);
        //储值
        Map storedBalanceDetailByDate = analysisMapper.queryStoredBalanceDetailByDate(startTime, endTime);
        balance.put("storedBalanceDetailByDate", storedBalanceDetailByDate);

        /**
         * 活跃用户
         */
        List<Map> userActCountByDay = analysisMapper.queryUserActCountByDay(startTime, endTime);
        if (userActCountByDay != null) {
            user.put("userActCountTotal",
                    userActCountByDay.stream().mapToLong(map ->
                            Long.valueOf(map.getOrDefault("act_user_cnt", "0").toString())
                    ).sum());
        }
        user.put("userActCountByDay", userActCountByDay);
        /**
         * 注册用户
         */
        List<Map> userRegCountByDay = analysisMapper.queryUserRegCountByDay(startTime, endTime);
        if (userRegCountByDay != null) {
            user.put("userRegCountTotal",
                    userRegCountByDay.stream().mapToLong(map ->
                            Long.valueOf(map.getOrDefault("reg_user_cnt", "0").toString())
                    ).sum());
        }
        user.put("userRegCountByDay", userRegCountByDay);


        /**
         * 时间区间内所有订单数量
         */
        Map orderCount = analysisMapper.queryOrderCount(startTime, endTime, null, null, null, null);
        order.put("orderCount", orderCount);

        Map orderCountPayed = analysisMapper.queryOrderCount(startTime, endTime, null, 1, null, null);
        order.put("orderCountPayed", orderCountPayed);

        /**
         * 按照状态分组
         */
        List<Map> countByStatus = analysisMapper.queryOrderCountByStatus(startTime, endTime);
        order.put("countByStatus", countByStatus);

        data.put("order", order);
        data.put("balance", balance);
        data.put("user", user);
        data.put("startTime", startTime);
        data.put("endTime", endTime);
        return data;
    }
}
