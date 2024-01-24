

package com.yami.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wechat.pay.java.service.partnerpayments.jsapi.model.Transaction;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import com.wechat.pay.java.service.refund.model.Refund;
import com.yami.shop.bean.app.param.PayParam;
import com.yami.shop.bean.model.UserBalanceOrder;
import com.yami.shop.bean.model.WxPayNotify;
import com.yami.shop.bean.model.WxPayPrepay;

import java.util.Map;

public interface WxPayPrepayService extends IService<WxPayPrepay> {

    /**
     * 创建用户充值预支付订单
     * @param userBalanceOrder
     * @param request
     * @param response
     * @author peiyuan.cai@
     * @date 2024/1/23 17:57 星期二
     */
    WxPayPrepay saveWxPayPrepayUserBalance(UserBalanceOrder userBalanceOrder, PrepayRequest request, PrepayWithRequestPaymentResponse response);


    /**
     *
     * @param payParam
     * @param request
     * @param response
     * @return
     * @author peiyuan.cai
     * @date 2024/1/24 14:27 星期三
     */
    WxPayPrepay saveWxPayPrepayGoods(PayParam payParam, PrepayRequest request, PrepayWithRequestPaymentResponse response);
}
