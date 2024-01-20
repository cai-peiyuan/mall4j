/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */

package com.yami.shop.api.controller;

import com.alibaba.fastjson2.JSONObject;
import com.yami.shop.bean.model.UserBalance;
import com.yami.shop.bean.model.UserBalanceDetail;
import com.yami.shop.bean.model.UserBalanceSell;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.security.api.util.SecurityUtils;
import com.yami.shop.service.UserBalanceDetailService;
import com.yami.shop.service.UserBalanceSellService;
import com.yami.shop.service.UserBalanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
