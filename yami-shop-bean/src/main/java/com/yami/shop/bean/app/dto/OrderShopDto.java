

package com.yami.shop.bean.app.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 订单下的每个店铺
 *
 * @author YaMi
 */
@Data
public class OrderShopDto implements Serializable {

    /**
     * 店铺ID
     **/
    @Schema(description = "店铺id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long shopId;

    /**
     * 店铺名称
     **/
    @Schema(description = "店铺名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String shopName;

    @Schema(description = "实际总值", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double actualTotal;

    @Schema(description = "商品总值", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double total;

    @Schema(description = "商品总数", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer totalNum;

    @Schema(description = "地址Dto", requiredMode = Schema.RequiredMode.REQUIRED)
    private UserAddrDto userAddrDto;

    @Schema(description = "产品信息", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<OrderItemDto> orderItemDtos;

    @Schema(description = "运费", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double transfee;

    @Schema(description = "优惠总额", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double reduceAmount;

    @Schema(description = "促销活动优惠金额", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double discountMoney;

    @Schema(description = "优惠券优惠金额", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double couponMoney;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "订单创建时间")
    private Date createTime;


    /**
     * 是否已经支付，1：已经支付过，0：，没有支付过
     */
    private Integer isPayed;

    /**
     * 付款时间
     */

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date payTime;
    /**
     * 订单备注信息
     */
    @Schema(description = "订单备注信息")
    private String remarks;

    /**
     * 订单状态
     */
    @Schema(description = "订单状态")
    private Integer status;
}
