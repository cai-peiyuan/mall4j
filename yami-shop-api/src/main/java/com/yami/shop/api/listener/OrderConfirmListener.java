package com.yami.shop.api.listener;

import com.yami.shop.bean.app.dto.ShopCartItemDto;
import com.yami.shop.bean.app.dto.ShopCartOrderDto;
import com.yami.shop.bean.app.param.OrderParam;
import com.yami.shop.bean.event.OrderConfirmEvent;
import com.yami.shop.bean.model.Product;
import com.yami.shop.bean.model.Sku;
import com.yami.shop.bean.model.UserAddr;
import com.yami.shop.bean.order.ConfirmOrderOrder;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.util.Arith;
import com.yami.shop.security.api.util.SecurityUtils;
import com.yami.shop.service.ProductService;
import com.yami.shop.service.SkuService;
import com.yami.shop.service.TransportManagerService;
import com.yami.shop.service.UserAddrService;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 确认订单信息时的默认操作
 *
 * @author LGH
 */
@Component
@AllArgsConstructor
public class OrderConfirmListener {

    private final UserAddrService userAddrService;

    private final TransportManagerService transportManagerService;

    private final ProductService productService;

    private final SkuService skuService;

    /**
     * 前端提交订单
     * 计算订单金额
     */
    @EventListener(OrderConfirmEvent.class)
    @Order(ConfirmOrderOrder.DEFAULT)
    public void defaultConfirmOrderEvent(OrderConfirmEvent event) {

        ShopCartOrderDto shopCartOrderDto = event.getShopCartOrderDto();

        //小程序端提交的下单参数
        OrderParam orderParam = event.getOrderParam();

        String userId = SecurityUtils.getUser().getUserId();

        // 订单的地址信息
        UserAddr userAddr = userAddrService.getUserAddrByUserId(orderParam.getAddrId(), userId);
        //订单总价格
        double total = 0.0;
        //订单不包含运费的总价
        double totalWithoutTransfee = 0.0;
        //总商品件数
        int totalCountWithoutTransfee = 0;
        //总商品件数
        int totalCount = 0;
        //运费总价
        double transfee = 0.0;


        /**
         * 计算订单不包含运费的总价
         */
        for (ShopCartItemDto shopCartItem : event.getShopCartItems()) {
            //订单不包含运费的总价
            totalWithoutTransfee = Arith.add(shopCartItem.getProductTotalAmount(), totalWithoutTransfee);

            //订单中各个商品总件数
            totalCountWithoutTransfee = shopCartItem.getProdCount() + totalCountWithoutTransfee;
        }

        /**
         * 不同的店铺商品计入不同的篮子
         */
        for (ShopCartItemDto shopCartItem : event.getShopCartItems()) {

            //检查商品和规格状态
            checkProdAndSku(shopCartItem);

            //订单中各个商品总件数
            totalCount = shopCartItem.getProdCount() + totalCount;
            //计算订单总金额
            total = Arith.add(shopCartItem.getProductTotalAmount(), total);
            // 用户地址如果为空，则表示该用户从未设置过任何地址相关信息
            if (userAddr != null) {
                // 每个产品的运费相加
                Double calculateTransfee = transportManagerService.calculateTransfee(shopCartItem, userAddr, totalWithoutTransfee, totalCountWithoutTransfee, transfee);
                transfee = Arith.add(transfee, calculateTransfee);
                totalWithoutTransfee = Arith.add(calculateTransfee, totalWithoutTransfee);
            }

            /**
             * 设置订单金额
             */
            shopCartItem.setActualTotal(shopCartItem.getProductTotalAmount());
            shopCartOrderDto.setActualTotal(Arith.add(total, transfee));
            shopCartOrderDto.setTotal(total);
            shopCartOrderDto.setTotalCount(totalCount);

            /**
             * 这里的订单运费需要重新计算 如果在同一个店铺下单的多个商品
             */
            shopCartOrderDto.setTransfee(transfee);
        }
    }

    /**
     * 检查商品和规格状态
     *
     * @param shopCartItem
     * @author peiyuan.cai
     * @date 2024/3/28 15:45 星期四
     */
    private void checkProdAndSku(ShopCartItemDto shopCartItem) {
        // 获取商品信息
        Product product = productService.getProductByProdId(shopCartItem.getProdId());
        // 获取sku信息
        Sku sku = skuService.getSkuBySkuId(shopCartItem.getSkuId());
        if (product == null || sku == null) {
            throw new YamiShopBindException("购物车包含未知的商品 商品编号[" + shopCartItem.getProdId() + "] 规格编号[" + shopCartItem.getSkuId() + "]");
        }
        if (product.getStatus() != 1 || sku.getStatus() != 1) {
            throw new YamiShopBindException("商品[" + sku.getProdName() + "]已下架");
        }
    }
}
