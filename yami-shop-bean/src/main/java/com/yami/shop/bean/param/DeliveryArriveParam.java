package com.yami.shop.bean.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 订单送达接口接收参数、
 *
 * @author c'p'y
 */
@Data
public class DeliveryArriveParam {

    @NotBlank(message = "订单号不能为空")
    @Schema(description = "订单号")
    private String orderNumber;

    @NotBlank(message = "快递公司id不能为空")
    @Schema(description = "快递公司")
    private Long dvyId;

    @Schema(description = "派送员id")
    private Long deliveryUserId;

    @NotBlank(message = "物流单号不能为空")
    @Schema(description = "物流单号")
    private String dvyFlowId;

    @Schema(description = "备注")
    private String remark;

}
