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
     * 根据appid和 openid获取用户信息
     *
     * @param appId
     * @param openId
     * @param session_key
     * @return
     */
    private User getUserByAppIdAndOpenId(String appId, String openId, String session_key) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getAppId, appId).eq(User::getOpenId, openId));
        if (user == null) {
            Date now = new Date();
            user = new User();
            user.setModifyTime(now);
            user.setUserRegtime(now);
            user.setStatus(1);
            // user.setNickName(openId);
            // user.setUserMail(openId);
            user.setUserId(openId);
            user.setOpenId(openId);
            user.setAppId(appId);
            user.setSessionKey(session_key);
            userService.save(user);
        } else {
            if (!StrUtil.equals(user.getSessionKey(), session_key)) {
                User upUser = new User();
                upUser.setUserId(user.getUserId());
                upUser.setSessionKey(session_key);
                userService.updateById(upUser);
            }
        }
        return user;
    }

    @PostMapping("/wxLogin")
    @Operation(summary = "微信小程序登录", description = "通过账号/手机号/用户名密码登录，还要携带用户的类型，也就是用户所在的系统")
    public ServerResponseEntity<Object> wxLogin(@Valid @RequestBody WxLoginDTO wxLoginDTO) {
        String wxLoginCode = wxLoginDTO.getWxLoginCode();
        String jscode2sessionUrl = "https://api.weixin.qq.com/sns/jscode2session";
        String secret = "032c5873129b0bef34e835c4c259e76c";
        String appId = "wxa6a1e944be3cc470";
        String grantType = "authorization_code";

        String jscode2SessionResultStr = HttpUtil.get(jscode2sessionUrl, new HashMap() {
            {
                this.put("appid", appId);
                this.put("secret", secret);
                this.put("grant_type", grantType);
                this.put("js_code", wxLoginCode);
            }
        });

        JSONObject jsonObject = JSON.parseObject(jscode2SessionResultStr);
        String openId = jsonObject.getString("openid");
        String session_key = jsonObject.getString("session_key");
        User user = getUserByAppIdAndOpenId(appId, openId, session_key);
        // 2. 登录
        UserInfoInTokenBO userInfoInTokenBO = new UserInfoInTokenBO();
        userInfoInTokenBO.setUserId(user.getUserId());
        userInfoInTokenBO.setNickName(user.getNickName());
        userInfoInTokenBO.setUserMobile(DesensitizedUtil.mobilePhone(user.getUserMobile()));
        userInfoInTokenBO.setPic(user.getPic());
        userInfoInTokenBO.setSysType(SysTypeEnum.ORDINARY.value());
        userInfoInTokenBO.setIsAdmin(0);
        userInfoInTokenBO.setEnabled(true);
        return ServerResponseEntity.success(tokenStore.storeAndGetVo(userInfoInTokenBO));
    }
}
