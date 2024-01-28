

package com.yami.shop.bean.param;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author lanhai
 */
@Data
public class DeliveryOrderParam {

	@NotBlank(message="订单号不能为空")
	@Schema(description = "订单号" ,required=true)
	private String orderNumber;

	@NotBlank(message="快递公司id不能为空")
	@Schema(description = "快递公司" ,required=true)
	private Long dvyId;

	@Schema(description = "派送员id")
	private Long deliveryUserId;

	@NotBlank(message="物流单号不能为空")
	@Schema(description = "物流单号" ,required=true)
	private String dvyFlowId;


}
