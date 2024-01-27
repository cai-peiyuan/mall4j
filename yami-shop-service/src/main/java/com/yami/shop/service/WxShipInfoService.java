package com.yami.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.shop.bean.model.*;

/**
 * 小程序发货信息管理
 *
 * @author peiyuan.cai
 * @date 2024/1/26 16:45 星期五
 */
public interface WxShipInfoService extends IService<WxShipInfo> {
    /**
     * 购物卡支付订单发货信息上传
     *
     * @param userBalanceOrder
     * @param wxPayPrepay
     * @return
     * @author peiyuan.cai.com
     * @date 2024/1/26 17:11 星期五
     */
    String uploadBalanceOrderShipInfo(UserBalanceOrder userBalanceOrder, WxPayPrepay wxPayPrepay);

    /**
     * 检查是否开通发货管理功能
     *
     * @return
     * @author peiyuan.cai.com
     * @date 2024/1/26 17:12 星期五
     */
    Boolean checkTradeManaged();

    /**
     * 充值订单虚拟发货
     *
     * @param orderNumber
     * @param userBalanceOrder
     */
    void uploadBalanceOrderShip(String orderNumber, UserBalanceOrder userBalanceOrder);

    /**
     * 上传订单物流信息到腾讯平台
     *
     * @param order
     */
    Boolean uploadOrderShipInfo(Order order, OrderSettlement orderSettlement);

    /**
     * 上传订单物流信息到腾讯平台
     *
     * @param order
     */
    Boolean uploadOrderShipInfo(Order order, OrderSettlement orderSettlement, WxPayPrepay wxPayPrepay);
}
