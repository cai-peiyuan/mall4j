

package com.yami.shop.bean.app.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author lanhai
 */
@Data
@Schema(description = "支付参数")
public class PayParam {

    /**
     * 订单号
     */
    @NotBlank(message = "订单号不能为空")
    @Schema(description = "订单号", required = true)
    private String orderNumbers;

    /**
     * 支付方式
     */
    @NotNull(message = "支付方式不能为空")
    @Schema(description = "支付方式 (1:微信支付 2:支付宝)", required = true)
    private Integer payType;
}
