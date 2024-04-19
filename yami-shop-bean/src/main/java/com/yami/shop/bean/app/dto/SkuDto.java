

package com.yami.shop.bean.app.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yami.shop.common.serializer.json.ImgJsonSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lanhai
 */
@Data
public class SkuDto implements Serializable {

    private static final long serialVersionUID = 6457261945829470666L;

    @Schema(description = "skuId" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Long skuId;
    @Schema(description = "价格" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Double price;
    @Schema(description = "原价" )
    private Double oriPrice;

    @Schema(description = "库存(-1表示无穷)" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer stocks;

    @Schema(description = "已售(-1表示1000+)" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer sales;

    @Schema(description = "sku名称" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String skuName;

    @Schema(description = "图片" )
    @JsonSerialize(using = ImgJsonSerializer.class)
    private String pic;

    @Schema(description = "销售属性组合字符串,格式是p1:v1;p2:v2" , requiredMode = Schema.RequiredMode.REQUIRED)
    private String properties;
}
