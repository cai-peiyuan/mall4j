

package com.yami.shop.bean.event;

import com.yami.shop.bean.app.dto.ShopCartItemDto;
import com.yami.shop.bean.app.dto.ShopCartOrderDto;
import com.yami.shop.bean.app.param.OrderParam;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 确认订单时的事件
 * @author LGH
 */
@Data
@AllArgsConstructor
public class OrderConfirmEvent {

    /**
     * 购物车已经组装好的店铺订单信息
     */
    private ShopCartOrderDto shopCartOrderDto;

    /**
     * 下单请求的参数
     */
    private OrderParam orderParam;

    /**
     * 店铺中的所有商品项
     */
    private List<ShopCartItemDto> shopCartItems;
}
