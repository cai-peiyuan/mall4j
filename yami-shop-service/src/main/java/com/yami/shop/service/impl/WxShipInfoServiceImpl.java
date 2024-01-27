

package com.yami.shop.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wechat.pay.java.service.partnerpayments.jsapi.model.Transaction;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import com.yami.shop.bean.app.param.PayParam;
import com.yami.shop.bean.model.UserBalanceOrder;
import com.yami.shop.bean.model.WxPayPrepay;
import com.yami.shop.bean.model.WxShipInfo;
import com.yami.shop.common.util.Json;
import com.yami.shop.dao.WxPayPrepayMapper;
import com.yami.shop.dao.WxShipInfoMapper;
import com.yami.shop.service.UserBalanceOrderService;
import com.yami.shop.service.UserBalanceService;
import com.yami.shop.service.WxPayPrepayService;
import com.yami.shop.service.WxShipInfoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static com.yami.shop.common.constants.Constant.KEY_SYS_CONFIG;

@Slf4j
@Service
@AllArgsConstructor
public class WxShipInfoServiceImpl extends ServiceImpl<WxShipInfoMapper, WxShipInfo> implements WxShipInfoService {
    private final RedisTemplate<String, Object> redisTemplate;

    private final UserBalanceOrderService userBalanceOrderService;


    private final WxPayPrepayService wxPayPrepayService;

    /**
     * 购物卡支付订单发货信息上传
     *
     * @param userBalanceOrder
     * @param wxPayPrepay
     * @return
     * @author peiyuan.cai.com
     * @date 2024/1/26 17:11 星期五
     */
    @Override
    public String uploadBalanceOrderShipInfo(UserBalanceOrder userBalanceOrder, WxPayPrepay wxPayPrepay) {
        WxShipInfo wxShipInfo = new WxShipInfo();
        Date now = new Date();
        /**
         * 发货信息录入接口
         * 调用方式以及出入参和HTTPS相同，仅是调用的token不同
         * 该接口所属的权限集id为：142
         * 服务商获得其中之一权限集授权后，可通过使用authorizer_access_token代商家进行调用
         * 请求参数
         */
        String ACCESS_TOKEN = (String) redisTemplate.opsForHash().get(KEY_SYS_CONFIG, "wxapp_access_token");
        String appId = (String) redisTemplate.opsForHash().get(KEY_SYS_CONFIG, "wxapp_appId");
        String wxpay_mchid = (String) redisTemplate.opsForHash().get(KEY_SYS_CONFIG, "wxpay_mchid");
        String url = "https://api.weixin.qq.com/wxa/sec/order/upload_shipping_info?access_token=" + ACCESS_TOKEN;
        JSONObject jsonObject = new JSONObject();
        JSONObject order_key = new JSONObject();
        order_key.put("order_number_type", 1);
        order_key.put("transaction_id", wxPayPrepay.getTransactionId());
        order_key.put("mchid", wxpay_mchid);
        order_key.put("out_trade_no", userBalanceOrder.getOrderNumber());
        jsonObject.put("order_key", order_key);
        /**
         * 物流模式，发货方式枚举值：1、实体物流配送采用快递公司进行实体物流配送形式 2、同城配送 3、虚拟商品，虚拟商品，例如话费充值，点卡等，无实体配送形式 4、用户自提
         */
        jsonObject.put("logistics_type", 3);
        /**
         * 发货模式，发货模式枚举值：1、UNIFIED_DELIVERY（统一发货）2、SPLIT_DELIVERY（分拆发货） 示例值: UNIFIED_DELIVERY
         */
        jsonObject.put("delivery_mode", 1);
        JSONArray shipping_list = new JSONArray();
        JSONObject shippingListItem = new JSONObject();
        String itemDesc = "储值"+userBalanceOrder.getTotal()+"已到账";
        shippingListItem.put("item_desc", itemDesc);
        shipping_list.add(shippingListItem);
        jsonObject.put("shipping_list", shipping_list);
        JSONObject payer = new JSONObject();
        payer.put("openid", userBalanceOrder.getUserId());
        jsonObject.put("payer", payer);
        String uploadTime = DateUtil.format(now, "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        jsonObject.put("upload_time", uploadTime);
        String post = HttpUtil.post(url, jsonObject.toJSONString());
        log.debug("小程序发货信息上传接口：" + post);
        JSONObject jsonObjResult = JSON.parseObject(post);
        Integer errcode = jsonObjResult.getInteger("errcode");
        String errmsg = jsonObjResult.getString("errmsg");

        wxShipInfo.setResultCode(errcode);
        wxShipInfo.setResultMsg(errmsg);
        wxShipInfo.setOrderNumberType(1);
        wxShipInfo.setTransactionId(wxPayPrepay.getTransactionId());
        wxShipInfo.setMchId(wxpay_mchid);
        wxShipInfo.setOutTradeNo(userBalanceOrder.getOrderNumber());
        wxShipInfo.setLogisticsType(3);
        wxShipInfo.setDeliveryMode(1);
        wxShipInfo.setItemDesc(itemDesc);
        wxShipInfo.setPayerId(userBalanceOrder.getUserId());
        wxShipInfo.setUpdateTime(now);
        wxShipInfo.setUploadTime(uploadTime);
        wxShipInfo.setCreateTime(now);
        wxShipInfo.setAppId(appId);
        if (errcode == 0) {
            log.debug("小程序发货信息上传结果：" + jsonObjResult);
            UserBalanceOrder balanceOrderUpdate = new UserBalanceOrder();
            balanceOrderUpdate.setOrderId(userBalanceOrder.getOrderId());
            balanceOrderUpdate.setShipTowx(1);
            balanceOrderUpdate.setStatus(5);
            boolean b = userBalanceOrderService.updateById(balanceOrderUpdate);
            log.debug("更新充值订单发货状态 {}", b);
            wxShipInfo.setDelivered(1);
            wxShipInfo.setReceipt(1);
        } else {
            log.error("小程序发货信息上传结果请求失败" + errmsg);
            wxShipInfo.setDelivered(0);
            wxShipInfo.setReceipt(0);
        }
        boolean save = save(wxShipInfo);
        log.debug("保存小程序发货数据记录 {}", save);
        return post;
    }

    /**
     * 检查是否开通发货管理功能
     *
     * @return
     * @author peiyuan.cai.com
     * @date 2024/1/26 17:12 星期五
     */
    @Override
    public Boolean checkTradeManaged() {
        boolean is_trade_managed = false;
        /**
         * 查询小程序是否已开通发货信息管理服务
         * 功能描述
         * 调用该接口可查询小程序账号是否已开通小程序发货信息管理服务（已开通的小程序，可接入发货信息管理服务API进行发货管理）。
         * https://developers.weixin.qq.com/miniprogram/dev/platform-capabilities/business-capabilities/order-shipping/order-shipping.html#%E5%85%AD%E3%80%81%E6%B6%88%E6%81%AF%E8%B7%B3%E8%BD%AC%E8%B7%AF%E5%BE%84%E8%AE%BE%E7%BD%AE%E6%8E%A5%E5%8F%A3
         *
         */
        String ACCESS_TOKEN = (String) redisTemplate.opsForHash().get(KEY_SYS_CONFIG, "wxapp_access_token");
        String appId = (String) redisTemplate.opsForHash().get(KEY_SYS_CONFIG, "wxapp_appId");
        String url = "https://api.weixin.qq.com/wxa/sec/order/is_trade_managed?access_token=" + ACCESS_TOKEN;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("appid", appId);
        String post = HttpUtil.post(url, jsonObject.toJSONString());

        log.debug("查询小程序是否已开通发货信息管理服务返回数据：" + post);
        JSONObject jsonObjResult = JSON.parseObject(post);
        Integer errcode = jsonObjResult.getInteger("errcode");
        if (errcode == 0) {
            is_trade_managed = jsonObjResult.getBooleanValue("is_trade_managed");
            log.debug("查询小程序是否已开通发货信息管理服务结果：" + is_trade_managed);
        } else {
            String errmsg = jsonObjResult.getString("errmsg");
            log.error("查询小程序是否已开通发货信息管理服务失败" + errmsg);
        }
        return is_trade_managed;
    }

    /**
     * 充值订单虚拟发货
     *
     * @param orderNumber
     * @param userBalanceOrder
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadBalanceOrderShip(String orderNumber, UserBalanceOrder userBalanceOrder) {
        if (orderNumber == null) {
            log.debug("orderNumber数据为空  ");
        }
        if (userBalanceOrder == null) {
            log.debug("userBalanceOrder数据为空  ");
            userBalanceOrder = userBalanceOrderService.getOne(new LambdaQueryWrapper<UserBalanceOrder>().eq(UserBalanceOrder::getOrderNumber, orderNumber));
        }
        log.debug("充值订单已支付 订单编号 {} ", orderNumber);
        WxPayPrepay wxPayPrepay = wxPayPrepayService.getOne(new LambdaQueryWrapper<WxPayPrepay>().eq(WxPayPrepay::getOutTradeNo, orderNumber).eq(WxPayPrepay::getTradeState, Transaction.TradeStateEnum.SUCCESS));
        if (wxPayPrepay == null) {
            log.debug("未查询到已支付的订单信息");
            return;
        }
        log.debug("查询到已支付的订单 {}", Json.toJsonString(wxPayPrepay));

        if (checkTradeManaged()) {
            log.debug("小程序已开通发货信息管理服务");
            log.debug("准备向腾讯推送虚拟发货信息");
            String s = uploadBalanceOrderShipInfo(userBalanceOrder, wxPayPrepay);
            log.debug("向腾讯推送虚拟发货信息请求接口返回 {}", s);
        } else {
            log.error("查询小程序是否已开通发货信息管理服务");
        }
    }
}
