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

import cn.hutool.core.bean.BeanUtil;
import com.yami.shop.bean.app.dto.UserDto;
import com.yami.shop.bean.app.param.BindPhoneParam;
import com.yami.shop.bean.app.param.UserInfoParam;
import com.yami.shop.bean.model.User;
import com.yami.shop.common.bean.Qiniu;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.security.api.util.SecurityUtils;
import com.yami.shop.service.AttachFileService;
import com.yami.shop.service.UserService;
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
    private UserService userService;

    @Autowired
    private AttachFileService attachFileService;

    @Autowired
    private Qiniu qiniu;

    /**
     * 查看用户接口
     */
    @GetMapping("/userInfo")
    @Operation(summary = "查看用户信息", description = "根据用户ID（userId）获取用户信息")
    public ServerResponseEntity<UserDto> userInfo() {
        String userId = SecurityUtils.getUser().getUserId();
        User user = userService.getById(userId);
        UserDto userDto = BeanUtil.copyProperties(user, UserDto.class);
        return ServerResponseEntity.success(userDto);
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
     * 上传小程序头像
     *
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/uploadAvatarImg")
    public ServerResponseEntity<String> uploadAvatarImg(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ServerResponseEntity.fail(-1,"文件为空");
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
