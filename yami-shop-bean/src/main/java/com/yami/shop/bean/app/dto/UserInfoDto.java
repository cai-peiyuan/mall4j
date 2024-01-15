/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */

package com.yami.shop.bean.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 用户基本信息
 *
 * @author c'p'y
 */
@Data
@Builder
public class UserInfoDto {

    @Schema(description = "用户编号")
    private String userId;

    @Schema(description = "用户昵称")
    private String nickName;

    @Schema(description = "用户名称")
    private String realName;

    @Schema(description = "用户手机号")
    private String userMobile;

    @Schema(description = "照片URL")
    private String pic;
}