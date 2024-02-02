package com.yami.shop.security.api.controller;

import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yami.shop.bean.model.User;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.util.PrincipalUtil;
import com.yami.shop.dao.UserMapper;
import com.yami.shop.security.common.bo.UserInfoInTokenBO;
import com.yami.shop.security.common.dto.AuthenticationDTO;
import com.yami.shop.security.common.dto.WxLoginDTO;
import com.yami.shop.security.common.enums.SysTypeEnum;
import com.yami.shop.security.common.manager.PasswordCheckManager;
import com.yami.shop.security.common.manager.PasswordManager;
import com.yami.shop.security.common.manager.TokenStore;
import com.yami.shop.security.common.vo.TokenInfoVO;
import com.yami.shop.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import com.yami.shop.common.response.ServerResponseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import java.util.Date;
import java.util.HashMap;

/**
 * @author 菠萝凤梨
 * @date 2022/3/28 15:20
 */
@RestController
@Tag(name = "登录")
public class LoginController {
    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordCheckManager passwordCheckManager;

    @Autowired
    private PasswordManager passwordManager;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @Operation(summary = "账号密码(用于前端登录)" , description = "通过账号/手机号/用户名密码登录，还要携带用户的类型，也就是用户所在的系统")
    public ServerResponseEntity<TokenInfoVO> login(
            @Valid @RequestBody AuthenticationDTO authenticationDTO) {
        String mobileOrUserName = authenticationDTO.getUserName();
        User user = getUser(mobileOrUserName);

        String decryptPassword = passwordManager.decryptPassword(authenticationDTO.getPassWord());

        // 半小时内密码输入错误十次，已限制登录30分钟
        passwordCheckManager.checkPassword(SysTypeEnum.ORDINARY,authenticationDTO.getUserName(), decryptPassword, user.getLoginPassword());

        UserInfoInTokenBO userInfoInToken = new UserInfoInTokenBO();
        userInfoInToken.setNickName(user.getNickName());
        userInfoInToken.setPic(user.getPic());
        userInfoInToken.setUserId(user.getUserId());
        userInfoInToken.setSysType(SysTypeEnum.ORDINARY.value());
        userInfoInToken.setEnabled(user.getStatus() == 1);
        // 存储token返回vo
        TokenInfoVO tokenInfoVO = tokenStore.storeAndGetVo(userInfoInToken);
        return ServerResponseEntity.success(tokenInfoVO);
    }

    private User getUser(String mobileOrUserName) {
        User user = null;
        // 手机验证码登陆，或传过来的账号很像手机号
        if (PrincipalUtil.isMobile(mobileOrUserName)) {
            user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUserMobile, mobileOrUserName));
        }
        // 如果不是手机验证码登陆， 找不到手机号就找用户名
        if  (user == null) {
            user = userMapper.selectOneByUserName(mobileOrUserName);
        }
        if (user == null) {
            throw new YamiShopBindException("账号或密码不正确");
        }
        return user;
    }


    /**
     * 微信小程序登录
     * @param wxLoginDTO
     * @return
     * @author peiyuan.cai
     * @date 2024/1/19 21:29 星期五
     */
    @PostMapping("/wxLogin")
    @Operation(summary = "微信小程序登录", description = "通过账号/手机号/用户名密码登录，还要携带用户的类型，也就是用户所在的系统")
    public ServerResponseEntity<Object> wxLogin(@Valid @RequestBody WxLoginDTO wxLoginDTO) {
        String wxLoginCode = wxLoginDTO.getWxLoginCode();
        // 1. 通过小程序获取的微信用户信息 将微信用户写入数据库 并返回数据库中的用户数据
        User user = userService.wxLogin(wxLoginCode);

        if(user != null){
            // 2. 登录
            UserInfoInTokenBO userInfoInTokenBO = new UserInfoInTokenBO();
            userInfoInTokenBO.setUserId(user.getUserId());
            userInfoInTokenBO.setNickName(user.getNickName());
            userInfoInTokenBO.setUserMobile(DesensitizedUtil.mobilePhone(user.getUserMobile()));
            userInfoInTokenBO.setUserMobile(user.getUserMobile());
            userInfoInTokenBO.setPic(user.getPic());
            userInfoInTokenBO.setSysType(SysTypeEnum.ORDINARY.value());
            userInfoInTokenBO.setIsAdmin(0);
            userInfoInTokenBO.setEnabled(true);
            //是否为工作人员标志
            userInfoInTokenBO.setIsStaff(user.getIsStaff());
            return ServerResponseEntity.success(tokenStore.storeAndGetVo(userInfoInTokenBO));
        }else{
            return ServerResponseEntity.showFailMsg("微信小程序用户登录失败，请检查AppId设置情况");
        }
    }
}
