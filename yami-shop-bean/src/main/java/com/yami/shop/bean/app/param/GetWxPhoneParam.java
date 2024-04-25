

package com.yami.shop.bean.app.param;

import io.swagger.v3.oas.annotations.media.Schema;
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
