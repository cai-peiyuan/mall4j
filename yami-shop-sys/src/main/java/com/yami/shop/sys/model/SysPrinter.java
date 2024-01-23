

package com.yami.shop.sys.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 系统配置信息
 *
 * @author lanhai
 */
@Data
@TableName("tz_sys_printer")
public class SysPrinter {
    @TableId
    private Long id;

    @NotBlank(message = "不能为空")
    private String userId;

    @NotBlank(message = "不能为空")
    private String clientId;

    @NotBlank(message = "不能为空")
    private String clientSecret;

    private String accessToken;
    private String machineCode;
    private String msign;
    private String machineState;
    private String version;
    private String printWidth;
    private String hardware;
    private String software;
    private String remark;

}
