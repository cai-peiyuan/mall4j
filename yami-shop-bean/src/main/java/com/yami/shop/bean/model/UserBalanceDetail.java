

package com.yami.shop.bean.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户余额明细
 *
 * @author c'p'y
 */
@Data
@TableName("tz_user_balance_detail")
public class UserBalanceDetail implements Serializable {
    private static final long serialVersionUID = 2090714647038636896L;
    @TableId
    private Long id;

    private String userId;

    private String detailType; //明细类型1余额2积分
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date useTime;
    private Double useBalance;
    private Double newBalance;
    private Integer useCredits;
    private Integer newCredits;
    private String orderNumber; //关联订单号
    private String description; //明细描述
}
