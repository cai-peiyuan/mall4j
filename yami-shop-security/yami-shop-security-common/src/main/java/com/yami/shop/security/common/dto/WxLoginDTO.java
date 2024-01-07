/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */
package com.yami.shop.security.common.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;


/**
 * 用于微信登陆传递
 *
 * @author cai
 * @date 22-11-30
 */
@Data
public class WxLoginDTO {

    /**
     * grantType
     */
    @NotBlank(message = "grantType不能为空")
    @Schema(description = "grantType", required = true)
    protected String grantType;

    /**
     * wxLoginCode
     */
    @NotBlank(message = "wxLoginCode不能为空")
    @Schema(description = "wxLoginCode", required = true)
    protected String wxLoginCode;

}