

package com.yami.shop.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qq.wechat.pay.WeChatPayUtil;
import com.qq.wechat.pay.config.WechatPaySign;
import com.wechat.pay.java.service.payments.jsapi.model.*;
import com.yami.shop.bean.model.UserBalanceOrder;
import com.yami.shop.bean.model.UserBalanceSell;
import com.yami.shop.bean.model.WxPayPrepay;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.util.Arith;
import com.yami.shop.dao.UserBalanceOrderMapper;
import com.yami.shop.dao.UserBalanceSellMapper;
import com.yami.shop.service.UserBalanceOrderService;
import com.yami.shop.service.WxPayPrepayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.yami.shop.common.constants.Constant.ORDER_TYPE_BALANCE;

/**
 * 微信储值订单
 *
 * @author peiyuan.cai
 * @date 2024/1/23 13:04 星期二
 */
@Service
public class UserBalanceOrderServiceImpl extends ServiceImpl<UserBalanceOrderMapper, UserBalanceOrder> implements UserBalanceOrderService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserBalanceOrderMapper userBalanceOrderMapper;

    @Autowired
    private UserBalanceSellMapper userBalanceSellMapper;
    @Autowired
    private Snowflake snowflake;

    @Autowired
    private WxPayPrepayService wxPayPrepayService;

    /**
     * 创建充值预支付订单
     *
     * @param userId
     * @param shopId
     * @param cardId
     */
    @Override
    public UserBalanceOrder createBalanceOrder(String userId, Long shopId, Long cardId) {
        Date now = new Date();

        //储值卡
        UserBalanceSell userBalanceSell = userBalanceSellMapper.selectById(cardId);
        if (userBalanceSell == null) {
            throw new YamiShopBindException("储值卡不存在 id = " + cardId);
        }
        UserBalanceOrder userBalanceOrder = new UserBalanceOrder();
        // 使用雪花算法生成的订单号
        String orderNumber = String.valueOf(snowflake.nextId());
        userBalanceOrder.setOrderNumber(orderNumber);
        userBalanceOrder.setUserId(userId);
        userBalanceOrder.setShopId(shopId);
        userBalanceOrder.setSellCardId(cardId);
        userBalanceOrder.setProdName("储值卡金额" + userBalanceSell.getStoredValue());
        userBalanceOrder.setCreateTime(now);
        userBalanceOrder.setActualTotal(userBalanceSell.getSellValue());
        userBalanceOrder.setTotal(userBalanceSell.getStoredValue());
        userBalanceOrder.setRemarks("用户储值卡");
        userBalanceOrder.setIsPayed(0);
        userBalanceOrder.setStatus(0);
        userBalanceOrder.setDeleteStatus(0);
        userBalanceOrder.setPayType(1);
        userBalanceOrder.setReduceAmount(Arith.sub(userBalanceSell.getStoredValue(), userBalanceSell.getSellValue()));
        save(userBalanceOrder);

        return userBalanceOrder;
    }

    /**
     * 创建微信预支付订单 并且返回支付参数
     *
     * @param userBalanceOrder
     * @return
     * @author peiyuan.cai
     * @date 2024/1/23 13:03 星期二
     */
    @Override
    public WechatPaySign createWeChatPayPreOrder(UserBalanceOrder userBalanceOrder) {
        // request.setXxx(val)设置所需参数，具体参数可见Request定义
        PrepayRequest request = new PrepayRequest();
        Amount amount = new Amount();
        amount.setTotal(Arith.toAmount(userBalanceOrder.getActualTotal()));
        request.setAmount(amount);
        request.setAppid(WeChatPayUtil.appId);
        request.setMchid(WeChatPayUtil.merchantId);
        request.setDescription(userBalanceOrder.getProdName());
        request.setNotifyUrl(WeChatPayUtil.WXPAY_NOTIFY_URL_TRANSACTION);
        request.setOutTradeNo(userBalanceOrder.getOrderNumber());
        Payer payer = new Payer();
        payer.setOpenid(userBalanceOrder.getUserId());
        request.setPayer(payer);
        //通过返回的消息通知中attach字段判断支付订单类型
        request.setAttach(ORDER_TYPE_BALANCE);
        PrepayWithRequestPaymentResponse response = WeChatPayUtil.jsapiServiceExtension.prepayWithRequestPayment(request);

        WechatPaySign wechatPaySign = new WechatPaySign();
        wechatPaySign.setSign(response.getPaySign());
        wechatPaySign.setNonceStr(response.getNonceStr());
        wechatPaySign.setTimeStamp(response.getTimeStamp());
        wechatPaySign.setPackageStr(response.getPackageVal());
        wechatPaySign.setPrepayId(response.getPackageVal());
        //保存预支付订单到数据库
        wxPayPrepayService.saveWxPayPrepayUserBalance(userBalanceOrder, request, response);
        return wechatPaySign;
    }
}
