

package com.yami.shop.bean.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 小程序用户基本信息
 *
 * @author c'p'y
 */
@Data
@Builder
public class UserInfoWxAppDto implements Serializable {

    @Schema(description = "用户编号")
    private String userId;

    @Schema(description = "用户昵称")
    private String nickName;

    @Schema(description = "用户名称")
    private String realName;

    @Schema(description = "用户手机号")
    private String userMobile;

    @Schema(description = "照片URL")
    private String pic;

    @Schema(description = "账户余额")
    private Double balance;

    @Schema(description = "积分")
    private Integer score;

    @Schema(description = "用户订单数量统计")
    private OrderCountData  orderCountData;

    @Schema(description = "用户收藏的商品数量")
    private Long  collectionCount;
}
