/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */

package com.yami.shop.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.StrKit;
import com.yami.shop.bean.app.dto.UserInfoDto;
import com.yami.shop.bean.app.param.BindPhoneParam;
import com.yami.shop.bean.app.param.GetWxPhoneParam;
import com.yami.shop.bean.enums.SmsType;
import com.yami.shop.bean.model.SmsLog;
import com.yami.shop.bean.model.User;
import com.yami.shop.bean.param.UserRegisterParam;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.util.IpHelper;
import com.yami.shop.common.util.RedisUtil;
import com.yami.shop.dao.SmsLogMapper;
import com.yami.shop.dao.UserMapper;
import com.yami.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

/**
 * @author lgh on 2018/09/11.
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SmsLogMapper smsLogMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    @Cacheable(cacheNames = "user", key = "#userId")
    public User getUserByUserId(String userId) {
        return userMapper.selectById(userId);
    }

    /**
     * 看看有没有校验验证码成功的标识
     *
     * @param userRegisterParam
     * @param checkRegisterSmsFlag
     */
    @Override
    public void validate(UserRegisterParam userRegisterParam, String checkRegisterSmsFlag) {
        if (StrUtil.isBlank(userRegisterParam.getCheckRegisterSmsFlag())) {
            // 验证码已过期，请重新发送验证码校验
            throw new YamiShopBindException("验证码已过期，请重新发送验证码校验");
        } else {
            String checkRegisterSmsFlagMobile = RedisUtil.get(checkRegisterSmsFlag);
            if (!Objects.equals(checkRegisterSmsFlagMobile, userRegisterParam.getMobile())) {
                // 验证码已过期，请重新发送验证码校验
                throw new YamiShopBindException("验证码已过期，请重新发送验证码校验");
            }
        }
    }


    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public void bindUserPhoneNum(String userId, BindPhoneParam bindPhoneParam) {
        SmsLog smsLog = smsLogMapper.selectOne(new LambdaQueryWrapper<SmsLog>().gt(SmsLog::getRecDate, DateUtil.beginOfDay(new Date())).lt(SmsLog::getRecDate, DateUtil.endOfDay(new Date())).eq(SmsLog::getUserId, userId).eq(SmsLog::getType, SmsType.VALID.value()).eq(SmsLog::getUserPhone, bindPhoneParam.getMobile()).eq(SmsLog::getStatus, 1));
        if (smsLog == null) {
            throw new YamiShopBindException("验证码已失效");
        }
        smsLog.setStatus(0);
        smsLogMapper.updateById(smsLog);
        User user = new User();
        user.setUserId(userId);
        user.setUserMobile(bindPhoneParam.getMobile());
        userMapper.updateById(user);
    }

    /**
     * 获取用户基础信息
     *
     * @param userId
     * @return
     */
    @Override
    public UserInfoDto getUserInfoById(String userId) {
        User user = getById(userId);
        return UserInfoDto.builder().userId(user.getUserId()).userMobile(user.getUserMobile()).pic(user.getPic()).realName(user.getRealName()).nickName(user.getNickName()).build();
    }

    /**
     * 微信小程序登录
     *
     * @param wxLoginCode
     * @return
     */
    @Override
    public User wxLogin(String wxLoginCode) {
        String jscode2sessionUrl = (String) redisTemplate.opsForHash().get("sys:config", "wxapp_jscode2sessionUrl");
        String secret = (String) redisTemplate.opsForHash().get("sys:config", "wxapp_secret");
        String appId = (String) redisTemplate.opsForHash().get("sys:config", "wxapp_appId");

        jscode2sessionUrl = StrKit.isBlank(jscode2sessionUrl) ? "https://api.weixin.qq.com/sns/jscode2session" : jscode2sessionUrl;
        secret = StrKit.isBlank(secret) ? "032c5873129b0bef34e835c4c259e76c" : secret;
        appId = StrKit.isBlank(appId) ? "wxa6a1e944be3cc470" : appId;
        String grantType = "authorization_code";
        String finalAppId = appId;
        String finalSecret = secret;
        String jscode2SessionResultStr = HttpUtil.get(jscode2sessionUrl, new HashMap() {
            {
                this.put("appid", finalAppId);
                this.put("secret", finalSecret);
                this.put("grant_type", grantType);
                this.put("js_code", wxLoginCode);
            }
        });
        JSONObject jsonObject = JSON.parseObject(jscode2SessionResultStr);
        String openId = jsonObject.getString("openid");
        String session_key = jsonObject.getString("session_key");
        //如果openid没有返回，则没有用户信息
        if (StrUtil.hasBlank(openId, session_key)) {
            return null;
        }
        User user = getUserByAppIdAndOpenId(appId, openId, session_key);
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
    @Override
    public User getUserByAppIdAndOpenId(String appId, String openId, String session_key) {
        //将数据库中open_id和 app_id列加上索引
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getAppId, appId).eq(User::getOpenId, openId));
        // 本地数据库中没有这个用户的话 需要将数据写入本地数据库
        Date now = new Date();
        if (user == null) {
            user = new User();
            user.setModifyTime(now);
            user.setUserRegtime(now);
            user.setUserLasttime(now);
            user.setUserLastip(IpHelper.getIpAddr());
            user.setStatus(1);
            // user.setNickName(openId);
            // user.setUserMail(openId);
            user.setUserId(openId);
            user.setOpenId(openId);
            user.setAppId(appId);
            user.setSessionKey(session_key);
            save(user);
        } else {
            //如果当前的sessionKey更新的话 更新到数据库中
            if (!StrUtil.equals(user.getSessionKey(), session_key)) {
                User upUser = new User();
                upUser.setUserId(user.getUserId());
                upUser.setUserLastip(IpHelper.getIpAddr());
                upUser.setUserLasttime(now);
                upUser.setSessionKey(session_key);
                updateById(upUser);

                user.setUserId(upUser.getUserId());
                user.setUserLastip(upUser.getUserLastip());
                user.setUserLasttime(upUser.getUserLasttime());
                user.setSessionKey(upUser.getSessionKey());
            }
        }
        return user;
    }

    /**
     * 获取用户的微信绑定手机号
     *
     * @param userId
     * @param getWxPhoneParam
     * @author peiyuan.cai@mapabc.com
     * @date 2024/1/19 21:52 星期五
     */
    @Override
    public void getWxPhoneParam(String userId, GetWxPhoneParam getWxPhoneParam) {

        /**
         * 获取accessToken
         */
        String ACCESS_TOKEN = (String) redisTemplate.opsForHash().get("sys:config", "wxapp_access_token");
        String url = "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=" + ACCESS_TOKEN;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", getWxPhoneParam.getCode());
        String post = HttpKit.post(url, jsonObject.toJSONString());
        /**
         * 错误码	错误描述	解决方案
         * -1	system error	系统繁忙，此时请开发者稍候再试
         * 40029	code 无效	js_code无效
         * 45011	api minute-quota reach limit  mustslower  retry next minute	API 调用太频繁，请稍候再试
         * 40013	invalid appid	请求appid身份与获取code的小程序appid不匹配
         * {
         *     "errcode":0,
         *     "errmsg":"ok",
         *     "phone_info": {
         *         "phoneNumber":"xxxxxx",
         *         "purePhoneNumber": "xxxxxx",
         *         "countryCode": 86,
         *         "watermark": {
         *             "timestamp": 1637744274,
         *             "appid": "xxxx"
         *         }
         *     }
         * }
         */
        JSONObject getUserPhoneResult = JSON.parseObject(post);
        /**
         * 请求结果中获取手机号码
         */
        Integer errcode = getUserPhoneResult.getInteger("errcode");
        if (errcode == 0) {

            JSONObject phone_info = getUserPhoneResult.getJSONObject("phone_info");
            String phoneNumber = phone_info.getString("phoneNumber");

            /**
             * 手机号码更新到数据库中
             */
            User user = new User();
            user.setUserId(userId);
            user.setUserMobile(phoneNumber);
            userMapper.updateById(user);
        }

    }
}
