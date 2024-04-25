

package com.yami.shop.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yami.shop.bean.model.IndexImg;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 统计和分析
 *
 * @author cai
 */
public interface AnalysisMapper {

    /**
     * 查询所有的储值余额总数
     *
     * @return
     * @author peiyuan.cai
     * @date 2024/4/25 12:47 星期四
     */
    Double queryBalanceSum();

    /**
     * 根据储值额度统计
     *
     * @return
     * @author peiyuan.cai
     * @date 2024/4/25 12:49 星期四
     */
    Map queryBalanceCountAndSumByValue(Double minBalance, Double maxBalance);

    /**
     * 储值统计
     *
     * @param startTime
     * @param endTime
     * @return
     * @author peiyuan.cai
     * @date 2024/4/25 13:06 星期四
     */
    Map queryStoredBalanceDetailByDate(Date startTime, Date endTime);

    /**
     * 消费统计
     *
     * @param startTime
     * @param endTime
     * @return
     * @author peiyuan.cai
     * @date 2024/4/25 13:07 星期四
     */
    Map queryUsedBalanceDetailByDate(Date startTime, Date endTime);

    /**
     * 查询统计新会员注册信息
     *
     * @param startTime
     * @param endTime
     * @return
     * @author peiyuan.cai
     * @date 2024/4/25 13:35 星期四
     */
    List<Map> queryUserRegCountByDay(Date startTime, Date endTime);

    /**
     * 查询统计会员活跃信息
     *
     * @param startTime
     * @param endTime
     * @return
     * @author peiyuan.cai
     * @date 2024/4/25 13:35 星期四
     */
    List<Map> queryUserActCountByDay(Date startTime, Date endTime);

    /**
     * 统计订单数量
     *
     * @param startTime
     * @param endTime
     * @param status
     * @param isPayed
     * @param payType
     * @param refundSts
     * @return
     * @author peiyuan.cai
     * @date 2024/4/25 13:45 星期四
     */
    Map queryOrderCount(Date startTime, Date endTime, Integer status, Integer isPayed, Integer payType, Integer refundSts);


    /**
     * 按照订单状态统计
     *
     * @param startTime
     * @param endTime
     * @return
     * @author peiyuan.cai
     * @date 2024/4/25 13:48 星期四
     */
    List<Map> queryOrderCountByStatus(Date startTime, Date endTime);
}
