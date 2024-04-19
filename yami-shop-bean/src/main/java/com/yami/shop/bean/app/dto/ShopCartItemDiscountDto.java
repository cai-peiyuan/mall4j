

package com.yami.shop.bean.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author lanhai
 */
@Data
public class ShopCartItemDiscountDto implements Serializable {


    @Schema(description = "已选满减项" , requiredMode = Schema.RequiredMode.REQUIRED)
    private ChooseDiscountItemDto chooseDiscountItemDto;

    @Schema(description = "商品列表" )
    private List<ShopCartItemDto> shopCartItems;
}
