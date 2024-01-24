

package com.yami.shop.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.shop.bean.app.dto.MyOrderDto;
import com.yami.shop.bean.app.dto.OrderShopDto;
import com.yami.shop.bean.model.Order;

/**
 * 我的订单
 * @author lgh
 */
public interface MyOrderService extends IService<Order> {

	/**
	 * 通过用户id和订单状态分页获取订单信息
	 * @param page   分页参数
	 * @param userId 用户id
	 * @param status 订单状态
	 * @return
	 */
	IPage<MyOrderDto> pageMyOrderByUserIdAndStatus(Page<MyOrderDto> page, String userId, Integer status);

	/**
	 * 根据用户名和订单号码查询订单信息
	 * @param userId
	 * @param orderNumber
	 * @return
	 * @author peiyuan.cai
	 * @date 2024/1/24 17:13 星期三
	 */
    OrderShopDto getMyOrderByOrderNumber(String userId, String orderNumber);

	/**
	 * 根据用户名和订单号码查询订单信息 v2
	 * @param userId
	 * @param orderNumber
	 * @return
	 * @author peiyuan.cai
	 * @date 2024/1/24 17:16 星期三
	 */
	JSONObject getMyOrderByOrderNumberV2(String userId, String orderNumber);
}
