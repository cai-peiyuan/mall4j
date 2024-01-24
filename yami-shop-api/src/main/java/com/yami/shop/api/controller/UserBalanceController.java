

package com.yami.shop.api.controller;

import com.alibaba.fastjson2.JSONObject;
import com.qq.wechat.pay.config.WechatPaySign;
import com.yami.shop.bean.model.*;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.security.api.model.YamiUser;
import com.yami.shop.security.api.util.SecurityUtils;
import com.yami.shop.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户余额
 *
 * @author c'p'y
 */
@RestController
@RequestMapping("/p/balance")
@Tag(name = "用户余额接口")
@AllArgsConstructor
public class UserBalanceController {

    private final UserBalanceService userBalanceService;

    private final PayService payService;
    private final UserBalanceOrderService userBalanceOrderService;
    private final UserBalanceSellService userBalanceSellService;
    private final UserBalanceDetailService userBalanceDetailService;

    /**
     * 加载账户余额
     */
    @GetMapping("/userBalanceAndSell")
    @Operation(summary = "加载账户余额")
    public ServerResponseEntity<Object> userBalanceAndSell() {
        UserBalance userBalance = userBalanceService.getUserBalanceByUserId(SecurityUtils.getUser().getUserId());
        JSONObject result = new JSONObject();
        result.put("userBalance", userBalance);
        List<UserBalanceSell> userBalanceSells = userBalanceSellService.listAll();
        result.put("userBalanceSells", userBalanceSells);
        return ServerResponseEntity.success(result);
    }

    /**
     * 加载账户余额明细
     */
    @GetMapping("/userBalanceDetail")
    @Operation(summary = "加载账户余额")
    public ServerResponseEntity<Object> userBalanceDetail() {
        List<UserBalanceDetail> userBalanceDetail = userBalanceDetailService.getUserBalanceDetailByUserId(SecurityUtils.getUser().getUserId());
        JSONObject result = new JSONObject();
        result.put("userBalanceDetail", userBalanceDetail);
        return ServerResponseEntity.success(result);
    }


    /**
     * 创建用户储值订单
     */
    @GetMapping("/createBalanceOrder/{cardId}")
    @Operation(summary = "创建用户储值订单")
    public ServerResponseEntity<WechatPaySign> createBalanceOrder(@PathVariable("cardId") Long cardId) {
        YamiUser user = SecurityUtils.getUser();
        // 创建储值订单
        UserBalanceOrder userBalanceOrder = userBalanceOrderService.createBalanceOrder(user.getUserId(), user.getShopId(), cardId);
        // 生成支付订单和支付参数
        WechatPaySign wechatPaySign = payService.createWeChatPrePayOrder(userBalanceOrder);
        return ServerResponseEntity.success(wechatPaySign);
    }

}
