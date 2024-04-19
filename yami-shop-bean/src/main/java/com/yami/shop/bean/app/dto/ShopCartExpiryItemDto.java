

package com.yami.shop.bean.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author lanhai
 */
@Data
@Schema(description = "购物车失效商品对象")
public class ShopCartExpiryItemDto {
    @Schema(description = "店铺ID" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long shopId;

    @Schema(description = "店铺名称" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String shopName;

    @Schema(description = "商品项" , requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ShopCartItemDto> shopCartItemDtoList;

}
