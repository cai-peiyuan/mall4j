

package com.yami.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wechat.pay.java.service.partnerpayments.jsapi.model.Transaction;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import com.yami.shop.bean.app.param.PayParam;
import com.yami.shop.bean.model.UserBalanceOrder;
import com.yami.shop.bean.model.WxPayPrepay;
import com.yami.shop.common.util.Json;
import com.yami.shop.dao.WxPayPrepayMapper;
import com.yami.shop.service.WxPayPrepayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class WxPayPrepayServiceImpl extends ServiceImpl<WxPayPrepayMapper, WxPayPrepay> implements WxPayPrepayService {

    /**
     * 创建用户充值预支付订单
     *
     * @param userBalanceOrder
     * @param request
     * @param response
     * @author peiyuan.cai@
     * @date 2024/1/23 17:57 星期二
     */
    @Override
    public WxPayPrepay saveWxPayPrepayUserBalance(UserBalanceOrder userBalanceOrder, PrepayRequest request, PrepayWithRequestPaymentResponse response) {
        WxPayPrepay wxPayPrepay = new WxPayPrepay();

        try {
            wxPayPrepay.setAppId(request.getAppid());
            wxPayPrepay.setMchId(request.getMchid());
            wxPayPrepay.setDescription(request.getDescription());
            //订单编号 多个
            wxPayPrepay.setOrderNumbers(request.getOutTradeNo());
            wxPayPrepay.setOutTradeNo(request.getOutTradeNo());
            wxPayPrepay.setAttach(request.getAttach());
            wxPayPrepay.setNotifyUrl(request.getNotifyUrl());
            wxPayPrepay.setGoodsTag(request.getGoodsTag());
            wxPayPrepay.setTotalAmount(request.getAmount().getTotal());
            wxPayPrepay.setPayerOpenid(request.getPayer().getOpenid());
            wxPayPrepay.setDetail(Json.toJsonString(request.getDetail()));
            wxPayPrepay.setSettleInfo(Json.toJsonString(request.getSettleInfo()));
            wxPayPrepay.setSceneInfo(Json.toJsonString(request.getSceneInfo()));
            wxPayPrepay.setSupportFapiao(Json.toJsonString(request.getSupportFapiao()));

            wxPayPrepay.setPrepayId(response.getPackageVal().replace("prepay_id=", ""));
            wxPayPrepay.setPrepayTimestamp(response.getTimeStamp());
            wxPayPrepay.setPrepayNonce(response.getNonceStr());
            wxPayPrepay.setPrepayPackage(response.getPackageVal());
            wxPayPrepay.setPrepaySignType(response.getSignType());

            wxPayPrepay.setCreateTime(new Date());
            wxPayPrepay.setOrderType(request.getAttach());
            wxPayPrepay.setTradeType(Transaction.TradeTypeEnum.JSAPI.name());
            wxPayPrepay.setTradeState(Transaction.TradeStateEnum.NOTPAY.name());
            wxPayPrepay.setTradeStateDesc("未支付");

            save(wxPayPrepay);
        } catch (Exception e) {
            log.error("保存微信预支付订单出错 " + e.getLocalizedMessage(), e);
        }
        return wxPayPrepay;
    }

    /**
     * @param payParam
     * @param request
     * @param response
     * @return
     * @author peiyuan.cai
     * @date 2024/1/24 14:27 星期三
     */
    @Override
    public WxPayPrepay saveWxPayPrepayGoods(PayParam payParam, PrepayRequest request, PrepayWithRequestPaymentResponse response) {
        WxPayPrepay wxPayPrepay = new WxPayPrepay();

        try {
            wxPayPrepay.setAppId(request.getAppid());
            wxPayPrepay.setMchId(request.getMchid());
            wxPayPrepay.setDescription(request.getDescription());
            //订单编号 多个
            wxPayPrepay.setOrderNumbers(payParam.getOrderNumbers());
            wxPayPrepay.setOutTradeNo(request.getOutTradeNo());
            wxPayPrepay.setAttach(request.getAttach());
            wxPayPrepay.setNotifyUrl(request.getNotifyUrl());
            wxPayPrepay.setGoodsTag(request.getGoodsTag());
            wxPayPrepay.setTotalAmount(request.getAmount().getTotal());
            wxPayPrepay.setPayerOpenid(request.getPayer().getOpenid());
            wxPayPrepay.setDetail(Json.toJsonString(request.getDetail()));
            wxPayPrepay.setSettleInfo(Json.toJsonString(request.getSettleInfo()));
            wxPayPrepay.setSceneInfo(Json.toJsonString(request.getSceneInfo()));
            wxPayPrepay.setSupportFapiao(Json.toJsonString(request.getSupportFapiao()));

            wxPayPrepay.setPrepayId(response.getPackageVal().replace("prepay_id=", ""));
            wxPayPrepay.setPrepayTimestamp(response.getTimeStamp());
            wxPayPrepay.setPrepayNonce(response.getNonceStr());
            wxPayPrepay.setPrepayPackage(response.getPackageVal());
            wxPayPrepay.setPrepaySignType(response.getSignType());
            wxPayPrepay.setPrepaySign(response.getPaySign());

            wxPayPrepay.setCreateTime(new Date());
            wxPayPrepay.setOrderType(request.getAttach());
            wxPayPrepay.setTradeType(Transaction.TradeTypeEnum.JSAPI.name());
            wxPayPrepay.setTradeState(Transaction.TradeStateEnum.NOTPAY.name());
            wxPayPrepay.setTradeStateDesc("未支付");

            save(wxPayPrepay);
        } catch (Exception e) {
            log.error("保存微信预支付订单出错 " + e.getLocalizedMessage(), e);
        }
        return wxPayPrepay;
    }
}
