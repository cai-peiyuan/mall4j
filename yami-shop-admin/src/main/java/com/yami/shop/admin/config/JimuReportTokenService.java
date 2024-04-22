package com.yami.shop.admin.config;

import com.yami.shop.security.admin.model.YamiSysUser;
import com.yami.shop.security.admin.util.SecurityUtils;
import com.yami.shop.security.common.bo.UserInfoInTokenBO;
import com.yami.shop.security.common.manager.TokenStore;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.jmreport.api.JmReportTokenServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义积木报表鉴权(如果不进行自定义，则所有请求不做权限控制)
 * 1.自定义获取登录token
 * 2.自定义获取登录用户
 *
 * @author Administrator
 */
@Slf4j
@Component
public class JimuReportTokenService implements JmReportTokenServiceI {
    @Value("${sa-token.token-name}")
    private String tokenName;
    @Autowired
    private TokenStore tokenStore;

    /**
     * 通过请求获取Token
     *
     * @param request
     * @return
     */
    @Override
    public String getToken(HttpServletRequest request) {
        //return TokenUtils.getTokenByRequest(request);
        String token = request.getHeader(tokenName);
        if (StringUtils.isBlank(token)) {
            token = request.getHeader("Token");
        }
        if (StringUtils.isBlank(token)) {
            token = request.getParameter(tokenName);
        }
        if (StringUtils.isBlank(token)) {
            token = request.getParameter("token");
        }
        log.debug("---------call---------getToken-----------------------   {}", token);
        return token;
    }


    /**
     * 通过Token获取登录人用户名
     *
     * @param token
     * @return
     */
    @Override
    public String getUsername(String token) {
        UserInfoInTokenBO info = tokenStore.getUserInfoByAccessToken(token, false);
        String userName = null == info ? null : info.getUserId();
        log.debug("JimuReportTokenService.getUsername -> {}", userName);
        return userName;
    }

    /**
     * Token校验
     *
     * @param token
     * @return
     */
    @Override
    public Boolean verifyToken(String token) {
        log.debug("---------verify-----Token---------------   {} ", token);
        //return TokenUtils.verifyToken(token, sysBaseAPI, redisUtil);
        String username = getUsername(token);
        return true;
    }

    @Override
    public Map<String, Object> getUserInfo(String token) {
        HashMap var2 = new HashMap(5);
        var2.put("sysUserCode", this.getUsername(token));
        var2.put("sysOrgCode", "");
        return var2;
    }

    @Override
    public String getTenantId() {
        return JmReportTokenServiceI.super.getTenantId();
    }

    @Override
    public String[] getRoles(String s) {
        return new String[0];
    }

    /**
     * 自定义请求头
     *
     * @return
     */
    @Override
    public HttpHeaders customApiHeader() {
        HttpHeaders header = new HttpHeaders();
        header.add("custom-header1", "Please set a custom value 1");
        header.add("token", "token value 2");
        return header;
    }
}
