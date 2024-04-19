

package com.yami.shop.bean.app.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * @author lanhai
 */
@Data
@Schema(description = "购物车物品参数")
public class OrderItemParam {

    @NotNull(message = "产品ID不能为空")
    @Schema(description = "产品ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long prodId;

    @NotNull(message = "skuId不能为空")
    @Schema(description = "skuId", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long skuId;

    @NotNull(message = "产品数量不能为空")
    @Min(value = 1, message = "产品数量不能为空")
    @Schema(description = "产品数量", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer prodCount;

    @NotNull(message = "店铺id不能为空")
    @Schema(description = "店铺id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long shopId;

    @Schema(description = "推广员使用的推销卡号")
    private String distributionCardNo;

    @Schema(description = "分享用户的编号")
    private String fromUserId;
}
