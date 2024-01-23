

package com.yami.shop.bean.app.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author lanhai
 */
@Data
public class OrderShopParam {

	/** 店铺ID **/
	@Schema(description = "店铺id" ,required=true)
	private Long shopId;

	/**
	 * 订单备注信息
	 */
	@Schema(description = "订单备注信息" ,required=true)
	private String remarks;
}
