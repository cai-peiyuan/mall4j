/*
 * Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.
 *
 * https://www.mall4j.com/
 *
 * 未经允许，不可做商业用途！
 *
 * 版权所有，侵权必究！
 */

package com.yami.shop.common.bean;

import com.yami.shop.common.enums.QiniuZone;
import lombok.Data;

/**
 * 易联云打印机配置参数
 * @author c'p'y
 */
@Data
public class YlyPrint {

	private String userId;

	private String clientId;

	private String clientSecret;

}
