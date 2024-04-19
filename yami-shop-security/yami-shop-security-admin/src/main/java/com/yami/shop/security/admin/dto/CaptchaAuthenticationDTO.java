package com.yami.shop.security.admin.dto;

import com.yami.shop.security.common.dto.AuthenticationDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 验证码登录
 *
 * @author 菠萝凤梨
 * @date 2022/3/28 14:57
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CaptchaAuthenticationDTO extends AuthenticationDTO {

    @Schema(description = "验证码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String captchaVerification;
}
