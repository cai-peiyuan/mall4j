package com.yami.shop.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.yami.shop.bean.app.dto.ProductItemDto;
import com.yami.shop.bean.enums.TransportChargeType;
import com.yami.shop.bean.model.*;
import com.yami.shop.common.util.Arith;
import com.yami.shop.common.util.Json;
import com.yami.shop.service.ProductService;
import com.yami.shop.service.SkuService;
import com.yami.shop.service.TransportManagerService;
import com.yami.shop.service.TransportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author lanhai
 */
@Service
@Slf4j
public class TransportManagerServiceImpl implements TransportManagerService {

    @Autowired
    private ProductService productService;
    @Autowired
    private SkuService skuService;
    @Autowired
    private TransportService transportService;


    /**
     * 根据订单信息和用户选择的地址信息计算运费
     *
     * @param productItem               订单中某个产品项
     * @param userAddr                  订单收货地址
     * @param totalWithoutTransfee      整个订单中不包含运费的总价 用于计算整个订单运费
     * @param totalCountWithoutTransfee 整个订单中商品总件数 用于计算整个订单运费
     * @param orderTransfee             整个订单已计算的运费  如果订单运费已经计算过了  就不再次计算运费了。
     * @return
     */
    @Override
    public Double calculateTransfee(ProductItemDto productItem, UserAddr userAddr, Double totalWithoutTransfee, int totalCountWithoutTransfee, double orderTransfee) {

        //订单商品项目总价
        Double productTotalAmount = productItem.getProductTotalAmount();
        Double compareTotalAmount = productTotalAmount.compareTo(totalWithoutTransfee) < 0 ? totalWithoutTransfee : productTotalAmount;
        // 商品信息
        Product product = productService.getProductByProdId(productItem.getProdId());
        // 用户所在城市id
        Long cityId = userAddr.getCityId();
        Long areaId = userAddr.getAreaId();

        log.debug("开始计算订单运费 产品编号 {} 名称 {} 收获地址 {} 整个订单不含运费总金额 {} 整个订单总商品数量 {} ", productItem.getProdId(), productItem.getProdName(), userAddr.getAddress(), totalWithoutTransfee, totalCountWithoutTransfee);
        log.debug("订单项价格 {} 用户计算订单运费的总价 {} ", productTotalAmount, compareTotalAmount);
        /**
         * product.getDeliveryMode()
         * hasUserPickUp 用户自提
         * hasShopDelivery 商家配送
         * {"hasUserPickUp": false, "hasShopDelivery": true}
         */
        Product.DeliveryModeVO deliveryModeVO = Json.parseObject(product.getDeliveryMode(), Product.DeliveryModeVO.class);

        // 没有店铺配送的方式
        if (!deliveryModeVO.getHasShopDelivery()) {
            log.debug("没有店铺配送的方式 {} 返回运费 0.0 ", product.getDeliveryMode());
            return 0.0;
        }

        /**
         * 运费模板编号
         * deliveryTemplateId : 58
         */
        if (product.getDeliveryTemplateId() == null) {
            log.debug("商品没有设置运费模板 {} 返回运费 0.0 ", product.getDeliveryTemplateId());
            return 0.0;
        }

        //找出该产品的运费项
        Transport transport = transportService.getTransportAndAllItems(product.getDeliveryTemplateId());
        /**
         * 运费模板不存在的情况 可能是 商家把运费模板删除
         */
        if (transport == null) {
            log.debug("商品运费模板数据为空 模板id {}  可能是商家删除了运费模板  返回运费 0.0 ", product.getDeliveryTemplateId());
            return 0.0;
        }

        Sku sku = skuService.getSkuBySkuId(productItem.getSkuId());

        // 用于计算运费的件数
        Double piece = getPiece(productItem, transport, sku);
        Double comparePiece = piece.compareTo(totalCountWithoutTransfee * 1.0) < 0 ? totalCountWithoutTransfee : piece;

        log.debug("订单项计算商品运费件数/重量/体积 {}  整个订单中商品数量 {} ", piece, comparePiece);

        //如果有包邮的条件
        if (transport.getHasFreeCondition() == 1) {
            log.debug("运费模板中设置了包邮条件 开始计算是否满足包邮条件");
            // 获取所有的包邮条件
            List<TransfeeFree> transfeeFrees = transport.getTransfeeFrees();
            for (TransfeeFree transfeeFree : transfeeFrees) {
                List<Area> freeCityList = transfeeFree.getFreeCityList();
                for (Area freeCity : freeCityList) {
                    /**
                     * 城市或者县区 都不包邮的情况下继续循环
                     */
                    if (!Objects.equals(freeCity.getAreaId(), cityId) && !Objects.equals(freeCity.getAreaId(), areaId)) {
                        continue;
                    }
                    /**
                     * 包邮方式 （0 满x件/重量/体积包邮 1满金额包邮 2满x件/重量/体积且满金额包邮）
                     */
                    boolean isFree = (transfeeFree.getFreeType() == 0 && comparePiece >= transfeeFree.getPiece())
                            || (transfeeFree.getFreeType() == 1 && compareTotalAmount >= transfeeFree.getAmount())
                            || (transfeeFree.getFreeType() == 2 && comparePiece >= transfeeFree.getPiece() && compareTotalAmount >= transfeeFree.getAmount());
                    if (isFree) {
                        log.debug("订单包邮类型 {} 包邮件数 {}  包邮金额{} 满足包邮条件 返回运费 0.0 ", transfeeFree.getFreeType(), transfeeFree.getPiece(), transfeeFree.getAmount());
                        return 0.0;
                    }
                }
            }
        }

        //订单的运费项
        Transfee transfee = null;
        List<Transfee> transfees = transport.getTransfees();
        for (Transfee dbTransfee : transfees) {
            // 将该商品的运费设置为默认运费
            if (transfee == null && CollectionUtil.isEmpty(dbTransfee.getCityList())) {
                transfee = dbTransfee;
            }
            // 如果在运费模板中的城市找到该商品的运费，则将该商品由默认运费设置为该城市的运费
            for (Area area : dbTransfee.getCityList()) {
                if (area.getAreaId().equals(cityId) || area.getAreaId().equals(areaId)) {
                    transfee = dbTransfee;
                    break;
                }
            }
            // 如果在运费模板中的城市找到该商品的运费，则退出整个循环
            if (transfee != null && CollectionUtil.isNotEmpty(transfee.getCityList())) {
                break;
            }
        }

        // 如果无法获取到任何运费相关信息，则返回0运费
        if (transfee == null) {
            log.debug("如果无法获取到任何运费相关信息 返回运费 0.0 ");
            return 0.0;
        }

        // 产品的运费
        Double fee = transfee.getFirstFee();
        // 如果件数大于首件数量，则开始计算超出的运费
        log.debug("用户计算运费的件数 {} 首件数量 {}", comparePiece, transfee.getFirstPiece());
        if (comparePiece > transfee.getFirstPiece()) {
            // 续件数量
            Double prodContinuousPiece = Arith.sub(piece, transfee.getFirstPiece());
            log.debug("续件数量 {} ", prodContinuousPiece);
            // 续件数量的倍数，向上取整
            Integer mulNumber = (int) Math.ceil(Arith.div(prodContinuousPiece, transfee.getContinuousPiece()));
            log.debug("续件数量的倍数 {} ", mulNumber);
            // 续件数量运费
            Double continuousFee = Arith.mul(mulNumber, transfee.getContinuousFee());
            if (orderTransfee > 0 && continuousFee > 0) {
                //续费在整个订单运费基础上续
                fee = continuousFee;
            } else {
                fee = Arith.add(fee, continuousFee);
            }
            log.debug("续件数量运费 {} 总运费 {}", continuousFee, fee);
        }
        return fee;
    }

    /**
     * 获取商品数量
     * 件数、重量、体积
     *
     * @param productItem
     * @param transport
     * @param sku
     * @return
     */
    private Double getPiece(ProductItemDto productItem, Transport transport, Sku sku) {
        Double piece = 0.0;
        if (Objects.equals(TransportChargeType.COUNT.value(), transport.getChargeType())) {
            // 按件数计算运费
            piece = Double.valueOf(productItem.getProdCount());
        } else if (Objects.equals(TransportChargeType.WEIGHT.value(), transport.getChargeType())) {
            // 按重量计算运费
            double weight = sku.getWeight() == null ? 0 : sku.getWeight();
            piece = Arith.mul(weight, productItem.getProdCount());
        } else if (Objects.equals(TransportChargeType.VOLUME.value(), transport.getChargeType())) {
            // 按体积计算运费
            double volume = sku.getVolume() == null ? 0 : sku.getVolume();
            piece = Arith.mul(volume, productItem.getProdCount());
        }
        return piece;
    }


}
