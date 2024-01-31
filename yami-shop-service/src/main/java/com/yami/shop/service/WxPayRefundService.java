

package com.yami.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import com.yami.shop.bean.app.param.PayParam;
import com.yami.shop.bean.model.UserBalanceOrder;
import com.yami.shop.bean.model.WxPayRefund;
import com.yami.shop.bean.model.WxPayRefund;

/**
 * @author c'p'y
 */
public interface WxPayRefundService extends IService<WxPayRefund> {

    /**
     * 创建用户充值退款订单
     * @param userBalanceOrder
     * @param request
     * @param response
     * @author peiyuan.cai
     */
    WxPayRefund saveWxPayRefundUserBalance(UserBalanceOrder userBalanceOrder, PrepayRequest request, PrepayWithRequestPaymentResponse response);


    /**
     *
     * @param payParam
     * @param request
     * @param response
     * @return
     * @author peiyuan.cai
     */
    WxPayRefund saveWxPayRefundGoods(PayParam payParam, PrepayRequest request, PrepayWithRequestPaymentResponse response);
}
