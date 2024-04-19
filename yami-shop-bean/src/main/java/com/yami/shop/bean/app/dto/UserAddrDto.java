

package com.yami.shop.bean.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lanhai
 */
@Data
public class UserAddrDto implements Serializable {
    @Schema(description = "地址id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long addrId;

    @Schema(description = "收货人", requiredMode = Schema.RequiredMode.REQUIRED)
    private String receiver;

    @Schema(description = "省", requiredMode = Schema.RequiredMode.REQUIRED)
    private String province;

    @Schema(description = "城市", requiredMode = Schema.RequiredMode.REQUIRED)
    private String city;

    @Schema(description = "区", requiredMode = Schema.RequiredMode.REQUIRED)
    private String area;

    @Schema(description = "地址", required = true)
    private String addr;

    @Schema(description = "手机", required = true)
    private String mobile;

    @Schema(description = "是否默认地址（1:是 0：否） ", required = true)
    private Integer commonAddr;

    /**
     * 省ID
     */
    @Schema(description = "省ID", required = true)
    private Long provinceId;

    /**
     * 城市ID
     */
    @Schema(description = "城市ID", required = true)
    private Long cityId;

    /**
     * 区域ID
     */
    @Schema(description = "区域ID", required = true)
    private Long areaId;


    /**
     * 验证地址数据合法性
     * @return
     */
    public boolean validAddress() {
        return receiver != null && province != null && city != null && area != null && addr != null && mobile != null && provinceId != null && cityId != null && areaId != null;
    }
}
