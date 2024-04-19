

package com.yami.shop.bean.app.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yami.shop.common.serializer.json.ImgJsonSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author lanhai
 */
@Schema(description = "我的订单-订单项")
@Data
public class MyOrderItemDto {

    @Schema(description = "商品图片" , requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonSerialize(using = ImgJsonSerializer.class)
    private String pic;

    @Schema(description = "商品名称" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String prodName;

    @Schema(description = "商品数量" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer prodCount;

    @Schema(description = "商品价格" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double price;

    @Schema(description = "skuName" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String skuName;

}
