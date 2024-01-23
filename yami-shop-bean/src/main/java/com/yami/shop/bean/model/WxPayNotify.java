

package com.yami.shop.bean.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 微信支付通知日志
 *
 * @author peiyuan.cai@
 * @date 2024/1/23 15:37 星期二
 */
@Data
@Builder
@TableName("tz_wx_pay_notify")
public class WxPayNotify implements Serializable {
    private static final long serialVersionUID = 2090714647038636896L;
    /**
     * ID
     */
    @TableId
    private Long id;

    private String notifyApi;

    private String reqBody;

    private String reqHeader;

    private String dataResource;

    private String dataResourceDecrypt;

    private String reqType;

    private String dataNotifyId;

    private String dataCreateTime;

    private String dataResourceType;

    private String dataEventType;

    private String dataSummary;

    private String notifyHandleResult;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


}
