

package com.yami.shop.bean.app.dto;

import com.yami.shop.bean.model.UserBalance;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 多个店铺订单合并在一起的合并类
 * "/confirm" 使用
 *
 * @author lanhai
 */
@Data
public class ShopCartOrderMergerDto implements Serializable {

    @Schema(description = "实际总值")
    private Double actualTotal;

    @Schema(description = "商品总值")
    private Double total;

    @Schema(description = "商品总数")
    private Integer totalCount;

    @Schema(description = "订单优惠金额(所有店铺优惠金额相加)")
    private Double orderReduce;

    @Schema(description = "地址Dto")
    private UserAddrDto userAddr;

    @Schema(description = "每个店铺的购物车信息")
    private List<ShopCartOrderDto> shopCartOrders;

    @Schema(description = "整个订单可以使用的优惠券列表")
    private List<CouponOrderDto> coupons;

    @Schema(description = "用户余额信息")
    private UserBalance userBalance;

    @Schema(description = "分享者用户编号" )
    private String fromUserId;
}
