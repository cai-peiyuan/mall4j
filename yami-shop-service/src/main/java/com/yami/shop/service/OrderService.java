

package com.yami.shop.service;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yami.shop.bean.app.dto.OrderCountData;
import com.yami.shop.bean.app.dto.ShopCartOrderMergerDto;
import com.yami.shop.bean.model.Order;
import com.yami.shop.bean.param.DeliveryArriveParam;
import com.yami.shop.bean.param.OrderParam;
import com.yami.shop.bean.param.OrderRefundParam;

import java.util.Date;
import java.util.List;

/**
 * @author lgh on 2018/09/15.
 */
public interface OrderService extends IService<Order> {

    /**
     * 根据订单编号获取订单
     * @param orderNumber
     * @return
     */
    Order getOrderByOrderNumber(String orderNumber);

    /**
     * 新增订单缓存
     * @param userId
     * @param shopCartOrderMergerDto
     * @return
     */
    ShopCartOrderMergerDto putConfirmOrderCache(String userId ,ShopCartOrderMergerDto shopCartOrderMergerDto);

    /**
     * 根据用户id获取订单缓存
     * @param userId
     * @return
     */
    ShopCartOrderMergerDto getConfirmOrderCache(String userId);

    /**
     * 根据用户id删除订单缓存
     * @param userId
     */
    void removeConfirmOrderCache(String userId);

    /**
     * 提交订单
     * @param userId
     * @param mergerOrder
     * @return
     */
    List<Order> submit(String userId, ShopCartOrderMergerDto mergerOrder);


    /**
     * 根据参数获取订单列表
     * @param orderStatus 订单状态
     * @param lessThanUpdateTime 更新时间
     * @return
     */
    List<Order> listOrderAndOrderItems(Integer orderStatus, DateTime lessThanUpdateTime);

    /**
     * 取消订单
     * @param orders
     */
    void cancelOrders(List<Order> orders);

    /**
     * 订单申请退款
     * 取消订单申请
     * @param orders
     */
    void refundApplyOrders(List<Order> orders);

    /**
     * 订单确认收货
     * @param orders
     */
    void confirmOrder(List<Order> orders);

    /**
     * 根据参数获取订单列表
     * @param order 订单参数
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    List<Order> listOrdersDetailByOrder(Order order, Date startTime, Date endTime);

    /**
     * 根据参数分页获取订单
     * @param page
     * @param orderParam
     * @return
     */
    IPage<Order> pageOrdersDetailByOrderParam(Page<Order> page, OrderParam orderParam);

    /**
     * 删除订单
     * @param orders
     */
    void deleteOrders(List<Order> orders);

    /**
     * 根据用户id获取各个状态的订单数目
     * @param userId
     * @return
     */
    OrderCountData getOrderCount(String userId);

    /**
     * 打印订单信息
     * @param order
     * @return
     */
    String printOrder(Order order);

    /**
     * 设置订单附加信息
     * @param order
     */
    void setOrderExtraInfo(Order order);

    /**
     * 订单发货
     * @param order
     * @param dvyId
     * @param dvyFlowId
     * @param deliveryUserId
     */
    void orderDelivery(Order order, Long dvyId, String dvyFlowId, Long deliveryUserId);

    /**
     * 针对某个订单退款
     * @param order
     */
    void refundOrder(Order order, OrderRefundParam refundForm);

    /**
     * 订单送达
     * @param order
     * @param deliveryArriveParam
     * @author peiyuan.cai@mapabc.com
     * @date 2024/2/5 17:31 星期一
     */
    void orderArrive(Order order, DeliveryArriveParam deliveryArriveParam);
}
