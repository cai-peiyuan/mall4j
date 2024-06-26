
package com.yami.shop.security.api.util;

import com.yami.shop.common.util.HttpContextUtils;
import com.yami.shop.security.api.model.YamiUser;
import com.yami.shop.security.common.bo.UserInfoInTokenBO;
import com.yami.shop.security.common.util.AuthUserContext;
import lombok.experimental.UtilityClass;

/**
 * @author LGH
 */
@UtilityClass
public class SecurityUtils {

    private static final String USER_REQUEST = "/p/";

    /**
     * 获取用户
     */
    public YamiUser getUser() {
        String requestURI = HttpContextUtils.getHttpServletRequest().getRequestURI();
        String requestURL = HttpContextUtils.getHttpServletRequest().getRequestURL().toString();
        String contextPath = HttpContextUtils.getHttpServletRequest().getContextPath();
        String servletPath = HttpContextUtils.getHttpServletRequest().getServletPath();
        if (!servletPath.startsWith(USER_REQUEST)) {
            // 用户相关的请求，应该以/p开头！！！
            throw new RuntimeException("yami.user.request.error");
        }
        UserInfoInTokenBO userInfoInTokenBO = AuthUserContext.get();

        YamiUser yamiUser = new YamiUser();
        yamiUser.setUserId(userInfoInTokenBO.getUserId());
        yamiUser.setBizUserId(userInfoInTokenBO.getBizUserId());
        yamiUser.setEnabled(userInfoInTokenBO.getEnabled());
        yamiUser.setShopId(userInfoInTokenBO.getShopId());
        yamiUser.setStationId(userInfoInTokenBO.getOtherId());
        yamiUser.setIsStaff(userInfoInTokenBO.getIsStaff());
        return yamiUser;
    }
}
