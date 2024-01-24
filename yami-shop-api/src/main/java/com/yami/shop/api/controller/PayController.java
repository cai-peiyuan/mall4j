

package com.yami.shop.api.controller;

import com.qq.wechat.pay.config.WechatPaySign;
import com.yami.shop.bean.app.param.PayParam;
import com.yami.shop.bean.model.UserBalanceOrder;
import com.yami.shop.bean.pay.PayInfoDto;
import com.yami.shop.security.api.model.YamiUser;
import com.yami.shop.security.api.util.SecurityUtils;
import com.yami.shop.service.PayService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import com.yami.shop.common.response.ServerResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lanhai
 */
@RestController
@RequestMapping("/p/order")
@Tag(name = "订单接口")
@AllArgsConstructor
public class PayController {

    private final PayService payService;

    /**
     * 小程序平台订单支付接口
     * 小程序下单后获取前端支付参数
     */
    @PostMapping("/pay")
    @Operation(summary = "根据订单号进行支付" , description = "根据订单号进行支付")
    public ServerResponseEntity<WechatPaySign> pay(@RequestBody PayParam payParam) {
        YamiUser user = SecurityUtils.getUser();
        String userId = user.getUserId();
        // 生成支付订单和支付参数
        WechatPaySign wechatPaySign = payService.createWeChatPrePayOrder(userId, user.getShopId(), payParam);
        /**
         * 这个是支付成功执行的操作 放入notify url接口中处理
         */
        //PayInfoDto payInfo = payService.pay(userId, payParam);
        //payService.paySuccess(payInfo.getPayNo(), "");

        // 创建储值订单
        //UserBalanceOrder userBalanceOrder = userBalanceOrderService.createBalanceOrder(user.getUserId(), user.getShopId(), cardId);
        // 生成支付订单和支付参数
        //WechatPaySign wechatPaySign = userBalanceOrderService.createWeChatPayPreOrder(userBalanceOrder);

        return ServerResponseEntity.success(wechatPaySign);
    }

    /**
     * 普通支付接口
     */
    @PostMapping("/normalPay")
    @Operation(summary = "根据订单号进行支付" , description = "根据订单号进行支付")
    public ServerResponseEntity<Boolean> normalPay(@RequestBody PayParam payParam) {

        YamiUser user = SecurityUtils.getUser();
        String userId = user.getUserId();
        PayInfoDto pay = payService.pay(userId, payParam);

        // 根据内部订单号更新order settlement
        payService.paySuccess(pay.getPayNo(), "");

        return ServerResponseEntity.success(true);
    }
}
