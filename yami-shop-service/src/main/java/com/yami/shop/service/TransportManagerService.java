

package com.yami.shop.service;

import com.yami.shop.bean.app.dto.ProductItemDto;
import com.yami.shop.bean.model.UserAddr;

/**
 * @author lanhai
 */
public interface TransportManagerService {
	/**
	 * 计算运费
	 * @param productItem 订单中某个产品项
	 * @param userAddr 订单收货地址
	 * @param totalWithoutTransfee 整个订单中不包含运费的总价 用于计算整个订单运费
	 * @param totalCountWithoutTransfee 整个订单中商品总件数 用于计算整个订单运费
	 * @param orderTransfee 整个订单已计算的运费
	 * @return
	 */
	Double calculateTransfee(ProductItemDto productItem, UserAddr userAddr, Double totalWithoutTransfee, int totalCountWithoutTransfee, double orderTransfee);
}
