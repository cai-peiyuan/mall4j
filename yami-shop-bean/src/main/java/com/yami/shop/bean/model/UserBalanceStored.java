

package com.yami.shop.bean.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 储值卡
 *
 * @author c'p'y
 */
@Data
@TableName("tz_user_balance_stored")
public class UserBalanceStored implements Serializable {
    private static final long serialVersionUID = 2090714647038636896L;
    /**
     * 单品ID
     */
    @TableId
    private Long id;

    /**
     * 店铺Id
     */
    private Long shopId;

    /**
     * 到账额度
     */
    private Double storedValue;

    /**
     * 售价
     */
    private Double sellValue;

    /**
     * 状态1可用2已使用0未销售
     */
    private Integer status;

    /**
     * 储值卡序列号
     */
    private String storedSn;

    /**
     * 储值卡使用用户id
     */
    private String storedUserId;

    /**
     * 储值卡使用用户手机号
     */
    private String storedUserMobile;

    /**
     * 状态1可用2已使用0未销售
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 使用时间
     */
    private Date storedTime;

    /**
     * 销售时间
     */
    private Date sellTime;

}
