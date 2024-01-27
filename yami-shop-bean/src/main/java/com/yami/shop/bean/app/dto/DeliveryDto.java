

package com.yami.shop.bean.app.dto;

import com.yami.shop.bean.model.DeliveryOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author lanhai
 */
@Data
public class DeliveryDto {

	@Schema(description = "物流公司名称")
	private String companyName;

	@Schema(description = "物流公司官网")
	private String companyHomeUrl;

	@Schema(description = "物流订单号")
	private String dvyFlowId;

	private DeliveryOrder deliveryOrder;

	@Schema(description = "查询出的物流信息")
	private List<DeliveryInfoDto> data;

}
