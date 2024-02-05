

package com.yami.shop.bean.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 * @author c'p'y
 */
@Data
public class OrderRefundParam {
    /**
     * 店铺id
     */
    private Long shopId;

    @Schema(description = "订单编号")
    private String orderNumber;

    private String refundMsg;

    private String sellerMsg;

    private String refundReason;

    @Schema(description = "申请类型(1:仅退款 2退款退货)")
    private Integer applyType;

}
