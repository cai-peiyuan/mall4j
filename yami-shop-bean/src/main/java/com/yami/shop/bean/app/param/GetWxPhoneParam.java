/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */

package com.yami.shop.bean.app.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Data;


@Data
@Schema(description = "获取微信用户手机号码参数")
public class GetWxPhoneParam {

    @Schema(description = "手机号获取凭证，由小程序点击获取手机号授权方法回调获取")
    private String code;

    @Schema(description = "encryptedData")
    private String encryptedData;

    @Schema(description = "errMsg")
    private String errMsg;

    @Schema(description = "iv")
    private String iv;

}
