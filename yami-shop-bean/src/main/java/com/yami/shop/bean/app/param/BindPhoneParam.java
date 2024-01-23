

package com.yami.shop.bean.app.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Data;


@Data
@Schema(description = "发送验证码参数")
public class BindPhoneParam {

    @Schema(description = "手机号")
    @Pattern(regexp = "1[0-9]{10}", message = "请输入正确的手机号")
    private String mobile;

    @Schema(description = "绑定app类型")
    private String appType;

    @Schema(description = "注册和绑定标识")
    private String registerOrBind;

    @Schema(description = "短信验证码")
    private String validCode;

    @Schema(description = "验证码类型")
    private String validateType;
}
