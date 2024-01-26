

package com.yami.shop.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qq.wechat.pay.WeChatPayUtil;
import com.qq.wechat.pay.config.WechatPaySign;
import com.wechat.pay.java.service.payments.jsapi.model.*;
import com.yami.shop.bean.enums.PayType;
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
        userBalanceOrder.setProdName("储值充值" + NumberUtil.decimalFormat("#.##", userBalanceSell.getStoredValue()));
        userBalanceOrder.setCreateTime(now);
        userBalanceOrder.setActualTotal(userBalanceSell.getSellValue());
        userBalanceOrder.setTotal(userBalanceSell.getStoredValue());
        userBalanceOrder.setRemarks("用户储值");
        userBalanceOrder.setIsPayed(0);
        userBalanceOrder.setStatus(0);
        userBalanceOrder.setDeleteStatus(0);
        userBalanceOrder.setPayType(PayType.WECHATPAY.value());
        userBalanceOrder.setReduceAmount(Arith.sub(userBalanceSell.getStoredValue(), userBalanceSell.getSellValue()));
        save(userBalanceOrder);

        return userBalanceOrder;
    }


}
