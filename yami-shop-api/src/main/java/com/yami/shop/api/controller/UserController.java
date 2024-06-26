

package com.yami.shop.api.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yami.shop.bean.app.dto.OrderCountData;
import com.yami.shop.bean.app.dto.UserDto;
import com.yami.shop.bean.app.dto.UserInfoDto;
import com.yami.shop.bean.app.param.BindPhoneParam;
import com.yami.shop.bean.app.param.GetWxPhoneParam;
import com.yami.shop.bean.app.param.UserInfoParam;
import com.yami.shop.bean.model.User;
import com.yami.shop.bean.model.UserBalance;
import com.yami.shop.bean.model.UserCollection;
import com.yami.shop.common.bean.Qiniu;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.security.api.util.SecurityUtils;
import com.yami.shop.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author lanhai
 */
@RestController
@RequestMapping("/p/user")
@Tag(name = "用户接口")
@AllArgsConstructor
public class UserController {


    @Autowired
    private OrderService orderService;

    @Autowired
    private UserBalanceService userBalanceService;

    @Autowired
    private UserCollectionService userCollectionService;

    @Autowired
    private UserService userService;

    @Autowired
    private AttachFileService attachFileService;

    @Autowired
    private Qiniu qiniu;

    /**
     * 查看用户接口
     */
    @Deprecated
    @GetMapping("/userInfo")
    @Operation(summary = "查看用户信息", description = "根据用户ID（userId）获取用户信息")
    public ServerResponseEntity<UserDto> userInfo() {
        String userId = SecurityUtils.getUser().getUserId();
        User user = userService.getById(userId);
        UserDto userDto = BeanUtil.copyProperties(user, UserDto.class);
        return ServerResponseEntity.success(userDto);
    }

    /**
     * 微信小程序用户获取用户信息接口
     */
    @Deprecated
    @GetMapping("/wxAppUserInfo")
    @Operation(summary = "获取小程序用户信息", description = "获取小程序用户信息")
    public ServerResponseEntity<Object> wxAppUserInfo() {
        // 从request中获取用户信息
        String userId = SecurityUtils.getUser().getUserId();
        // 获取用户详细信息
        User user = userService.getById(userId);
        // 将基本信息复制到dto中
        UserInfoDto userDto = BeanUtil.copyProperties(user, UserInfoDto.class);

        JSONObject result = new JSONObject();
        // 获取订单数量信息
        OrderCountData orderCountMap = orderService.getOrderCount(userId);
        result.put("orderCountData", orderCountMap);

        // 获取收藏夹数量
        long collectionCount = userCollectionService.count(new LambdaQueryWrapper<UserCollection>().eq(UserCollection::getUserId, userId));
        result.put("collectionCount", collectionCount);

        //获取用户余额
        //获取用户积分
        UserBalance userBalance = userBalanceService.getUserBalanceAddIfNotExists(userId, userDto.getUserMobile());
        result.put("userBalance", userBalance);

        //获取用户优惠券数量
        result.put("userInfo", userDto);
        return ServerResponseEntity.success(result);
    }

    @PutMapping("/setUserInfo")
    @Operation(summary = "设置用户信息", description = "设置用户信息")
    public ServerResponseEntity<Void> setUserInfo(@RequestBody UserInfoParam userInfoParam) {
        String userId = SecurityUtils.getUser().getUserId();
        User user = new User();
        user.setUserId(userId);
        user.setPic(userInfoParam.getAvatarUrl());
        user.setNickName(userInfoParam.getNickName());
        userService.updateById(user);
        return ServerResponseEntity.success();
    }

    /**
     * 绑定用户手机号
     *
     * @param bindPhoneParam
     * @return
     */
    @PutMapping("/bindUserPhoneNum")
    @Operation(summary = "绑定用户手机号", description = "绑定用户手机号")
    public ServerResponseEntity<Void> bindUserPhoneNum(@RequestBody BindPhoneParam bindPhoneParam) {
        String userId = SecurityUtils.getUser().getUserId();
        userService.bindUserPhoneNum(userId, bindPhoneParam);
        return ServerResponseEntity.success();
    }


    /**
     * 获取用户的微信绑定手机号
     *
     * @param getWxPhoneParam
     * @return
     */
    @PostMapping("/getWxPhoneNumber")
    @Operation(summary = "获取用户的微信绑定手机号", description = "获取用户的微信绑定手机号")
    public ServerResponseEntity<Void> getWxPhoneNumber(@RequestBody GetWxPhoneParam getWxPhoneParam) {
        String userId = SecurityUtils.getUser().getUserId();
        userService.getWxPhoneParam(userId, getWxPhoneParam);
        return ServerResponseEntity.success();
    }


    /**
     * 上传小程序头像
     *
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/uploadAvatarImg")
    public ServerResponseEntity<String> uploadAvatarImg(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ServerResponseEntity.fail(-1, "文件为空");
        }
        String userId = SecurityUtils.getUser().getUserId();
        User user = new User();
        user.setUserId(userId);
        String fileName = attachFileService.uploadFile(file.getBytes(), file.getOriginalFilename());
        String avatarUrl = qiniu.getResourcesUrl() + fileName;
        user.setPic(avatarUrl);
        userService.updateById(user);
        return ServerResponseEntity.success(avatarUrl);
    }
}
