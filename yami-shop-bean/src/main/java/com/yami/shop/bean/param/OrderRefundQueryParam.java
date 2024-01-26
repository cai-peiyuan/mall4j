

package com.yami.shop.bean.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 */
@Data
public class OrderRefundQueryParam {
    /**
     * 店铺id
     */
    private Long shopId;

    @Schema(description = "订单编号" , required = true)
    private String orderNumber;

    @Schema(description = "申请类型(1:仅退款 2退款退货)" , required = true)
    private Integer applyType;

}
