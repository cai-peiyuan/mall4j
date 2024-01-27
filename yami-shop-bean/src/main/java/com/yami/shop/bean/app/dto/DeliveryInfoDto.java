

package com.yami.shop.bean.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author lanhai
 */
@Data
public class DeliveryInfoDto {
	
	@Schema(description = "详细信息")
	private String context;
	
	private String ftime;
	
	@Schema(description = "快递所在区域")
	private String location;
	
	@Schema(description = "物流更新时间")
	private String time;
	
}
