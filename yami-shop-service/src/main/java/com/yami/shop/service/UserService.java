

package com.yami.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.shop.bean.app.dto.UserInfoDto;
import com.yami.shop.bean.app.param.BindPhoneParam;
import com.yami.shop.bean.app.param.GetWxPhoneParam;
import com.yami.shop.bean.model.User;
import com.yami.shop.bean.param.UserRegisterParam;

/**
 *
 * @author lgh on 2018/09/11.
 */
public interface UserService extends IService<User> {
    /**
     * 根据用户id获取用户信息
     *
     * @param userId
     * @return
     */
    User getUserByUserId(String userId);

    /**
     * 校验验证码
     *
     * @param userRegisterParam
     * @param checkRegisterSmsFlag
     */
    void validate(UserRegisterParam userRegisterParam, String checkRegisterSmsFlag);

    /**
     * 绑定微信用户手机号
     *
     * @param userId
     * @param bindPhoneParam
     */
    void bindUserPhoneNum(String userId, BindPhoneParam bindPhoneParam);

    /**
     * 获取用户基础信息
     *
     * @param userId
     * @return
     */
    UserInfoDto getUserInfoById(String userId);

    /**
     * 微信小程序登录
     *
     * @param wxLoginCode
     * @return
     */
    User wxLogin(String wxLoginCode);


    /**
     * 通过小程序的appid和登录小程序的用户openid 获取本地数据库中存储的用户信息
     * @param appId
     * @param openId
     * @param session_key
     * @return
     */
    User getUserByAppIdAndOpenId(String appId, String openId, String session_key);

    /**
     * 获取用户的微信绑定手机号
     * @param userId
     * @param getWxPhoneParam
     * @author peiyuan.cai
     * @date 2024/1/19 21:52 星期五
     */
    void getWxPhoneParam(String userId, GetWxPhoneParam getWxPhoneParam);
}
