package com.yami.shop.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.jfinal.kit.Kv;
import com.jfinal.template.Engine;
import com.yami.shop.bean.app.dto.OrderCountData;
import com.yami.shop.bean.app.dto.ShopCartOrderMergerDto;
import com.yami.shop.bean.app.dto.UserInfoDto;
import com.yami.shop.bean.enums.OrderStatus;
import com.yami.shop.bean.enums.PayType;
import com.yami.shop.bean.event.*;
import com.yami.shop.bean.model.*;
import com.yami.shop.bean.param.DeliveryArriveParam;
import com.yami.shop.bean.param.OrderParam;
import com.yami.shop.bean.param.OrderRefundParam;
import com.yami.shop.common.exception.YamiShopBindException;
import com.yami.shop.common.util.PageAdapter;
import com.yami.shop.dao.*;
import com.yami.shop.service.*;
import com.yly.print_sdk_library.RequestMethod;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.yami.shop.common.constants.Constant.KEY_SYS_CONFIG;

/**
 * @author lgh on 2018/09/15.
 */
@Service
@AllArgsConstructor
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {


    private final WxShipInfoService wxShipInfoService;
    private final DeliveryOrderService deliveryOrderService;
    private final DeliveryUserService deliveryUserService;
    private final DeliveryOrderRouteService deliveryOrderRouteService;
    private final UserService userService;
    private final OrderMapper orderMapper;
    private final CommonMapper commonMapper;
    private final SkuMapper skuMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final UserAddrOrderService userAddrOrderService;
    private final OrderItemService orderItemService;
    private final OrderSettlementService orderSettlementService;

    private final OrderRefundService orderRefundService;
    private final Snowflake snowflake;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 从文件中读取Java对象
     *
     * @return order
     */
    public static Order testReadObject() {
        try {
            // 创建输入流并指定文件路径
            FileInputStream fileIn = new FileInputStream("order.javaObj");
            // 创建对象输入流
            ObjectInputStream in = new ObjectInputStream(fileIn);
            // 从文件读取对象
            Order order = (Order) in.readObject();
            // 关闭输入流
            in.close();
            fileIn.close();
            // 输出读取的对象
            System.out.println(order);
            return order;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将Java对象写入到一个文件中
     *
     * @param order
     */
    public static void testWriteObject(Order order) {
        try {
            // 创建输出流并指定文件路径
            FileOutputStream fileOut = new FileOutputStream("order.javaObj");
            // 创建对象输出流
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            // 将对象写入文件
            out.writeObject(order);
            // 关闭输出流
            out.close();
            fileOut.close();
            System.out.println("对象已成功写入文件！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据设置的打印模板生成打印订单小票内容
     *
     * @param print_order_content_template
     * @param order
     * @return
     */
    public static String renderOrderPrintContent(String print_order_content_template, Order order) {
        //默认配置就够用了
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig());
        //资源，根据实现不同，此资源可以是模板本身，也可以是模板的相对路径
        Template template = engine.getTemplate(print_order_content_template);
        //给STRING_TEMPLATE绑定数据
        Map<String, Object> bindingMap = new HashMap<>(8);
        bindingMap.put("order", order);
//        bindingMap.put("plateNo", plateNo);
//        bindingMap.put("plateColor", plateColor);
//        bindingMap.put("passTime", passTime);
//        bindingMap.put("crossName", crossName);
        //最终渲染出来的样子
        String text = template.render(bindingMap);
        return text;
    }

    /**
     * 根据设置的打印模板生成打印订单小票内容
     *
     * @param print_order_content_template
     * @return
     */
    public static String renderOrderPrintContentEnjoy(String print_order_content_template, Order order, Map map) {
        Engine engine = Engine.use();
        engine.setStaticMethodExpression(true);
        engine.setDevMode(true);
        engine.setToClassPathSourceFactory();
        com.jfinal.template.Template template = engine.getTemplateByString(print_order_content_template);
        String result = template.renderToString(Kv.by("order", order).set(map));
        return result;
    }

    @Override
    public Order getOrderByOrderNumber(String orderNumber) {
        return orderMapper.getOrderByOrderNumber(orderNumber);
    }

    @Override
    @CachePut(cacheNames = "ConfirmOrderCache", key = "#userId")
    public ShopCartOrderMergerDto putConfirmOrderCache(String userId, ShopCartOrderMergerDto shopCartOrderMergerDto) {
        //仅仅是将shopCartOrderMergerDto 放入缓存
        return shopCartOrderMergerDto;
    }

    @Override
    @Cacheable(cacheNames = "ConfirmOrderCache", key = "#userId")
    public ShopCartOrderMergerDto getConfirmOrderCache(String userId) {
        //从缓存中返回数据 不执行返回这个null
        return null;
    }

    /**
     * 根据用户编号删除订单预提交缓存
     * 每个用户在提交订单之前添加一个缓存 下单后移除缓存
     * 每个用户只有一份缓存数据
     *
     * @param userId
     */
    @Override
    @CacheEvict(cacheNames = "ConfirmOrderCache", key = "#userId")
    public void removeConfirmOrderCache(String userId) {
    }

    /**
     * 提交订单并且
     * 生成订单信息
     * @param userId
     * @param mergerOrder
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Order> submit(String userId, ShopCartOrderMergerDto mergerOrder) {
        List<Order> orderList = new ArrayList<>();
        // 通过事务提交订单
        eventPublisher.publishEvent(new OrderSubmitEvent(mergerOrder, orderList));
        // 批量写入插入订单
        this.saveBatch(orderList);
        // 取出所有订单项目
        List<OrderItem> orderItems = orderList.stream().flatMap(order -> order.getOrderItems().stream()).collect(Collectors.toList());
        // 插入订单项，返回主键
        orderItemMapper.insertBatch(orderItems);
        return orderList;
    }

    @Override
    public List<Order> listOrderAndOrderItems(Integer orderStatus, DateTime lessThanUpdateTime) {
        return orderMapper.listOrderAndOrderItems(orderStatus, lessThanUpdateTime);
    }

    /**
     * 取消订单
     *
     * @param orders
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrders(List<Order> orders) {
        orderMapper.cancelOrders(orders);
        List<OrderItem> allOrderItems = new ArrayList<>();
        for (Order order : orders) {
            List<OrderItem> orderItems = order.getOrderItems();
            allOrderItems.addAll(orderItems);
            eventPublisher.publishEvent(new OrderCancelEvent(order));
        }
        if (CollectionUtil.isEmpty(allOrderItems)) {
            return;
        }
        Map<Long, Integer> prodCollect = new HashMap<>(16);
        Map<Long, Integer> skuCollect = new HashMap<>(16);

        allOrderItems.stream().collect(Collectors.groupingBy(OrderItem::getProdId)).forEach((prodId, orderItems) -> {
            int prodTotalNum = orderItems.stream().mapToInt(OrderItem::getProdCount).sum();
            prodCollect.put(prodId, prodTotalNum);
        });
        // 增加商品库存
        productMapper.returnStock(prodCollect);

        allOrderItems.stream().collect(Collectors.groupingBy(OrderItem::getSkuId)).forEach((skuId, orderItems) -> {
            int prodTotalNum = orderItems.stream().mapToInt(OrderItem::getProdCount).sum();
            skuCollect.put(skuId, prodTotalNum);
        });
        // 增加商品属性的库存
        skuMapper.returnStock(skuCollect);
    }

    /**
     * 申请退款
     *
     * @param orders
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refundApplyOrders(List<Order> orders) {
        Date now = new Date();
        for (Order order : orders) {
            if (order.getStatus() != OrderStatus.PADYED.value()) {
                throw new YamiShopBindException("订单" + order.getOrderNumber() + "状态不正确，无法申请退款");
            }

            if (order.getIsPayed() != 1) {
                throw new YamiShopBindException("订单" + order.getOrderNumber() + "没有付款，无法申请退款");
            }
            /**
             * 订单已支付并且是微信支付 或者 余额支付
             */
            if (order.getPayType() == PayType.WECHATPAY.value() || order.getPayType() == PayType.BALANCE.value()) {
                OrderSettlement settlement = orderSettlementService.getOne(new LambdaQueryWrapper<OrderSettlement>().eq(OrderSettlement::getOrderNumber, order.getOrderNumber()));

                /**
                 * 写入退款订单
                 */
                OrderRefund orderRefund = new OrderRefund();
                orderRefund.setShopId(order.getShopId());
                orderRefund.setOrderId(order.getOrderId());
                orderRefund.setOrderNumber(order.getOrderNumber());
                orderRefund.setOrderAmount(order.getActualTotal());
                orderRefund.setOrderItemId(0L);
                orderRefund.setUserId(order.getUserId());
                orderRefund.setShopId(order.getShopId());

                orderRefund.setOrderPayNo(settlement.getPayNo());
                orderRefund.setBizPayNo(settlement.getBizPayNo());
                orderRefund.setPayType(settlement.getPayType());
                orderRefund.setPayTypeName(settlement.getPayTypeName());
                orderRefund.setRefundAmount(settlement.getPayAmount());
                // 平台自己的退款
                String outRefundNo = String.valueOf(snowflake.nextId());
                orderRefund.setOutRefundNo(outRefundNo);

                //申请类型:1,仅退款,2退款退货
                orderRefund.setApplyType(2);
                // 处理状态:1为待审核,2为同意,3为不同意
                orderRefund.setRefundSts(1);
                //处理退款状态: 0:退款处理中 1:退款成功 -1:退款失败
                orderRefund.setReturnMoneySts(0);
                orderRefund.setApplyTime(now);
                orderRefund.setBuyerMsg("已付款且未发货订单退款");
                orderRefundService.save(orderRefund);

                /**
                 * 更新订单退款状态
                 *
                 */
                Order upOrder = new Order();
                //0:默认,1:在处理,2:处理完成
                upOrder.setRefundSts(1);
                //订单关闭原因 1-超时未支付 2-退款关闭 4-买家取消 15-已通过货到付款交易
                upOrder.setCloseType(2);
                upOrder.setStatus(6);
                upOrder.setCancelTime(now);
                upOrder.setOrderId(order.getOrderId());
                updateById(upOrder);

                /**
                 * 广播订单退款事件
                 */
                eventPublisher.publishEvent(new OrderRefundApplyEvent(order));
            } else if (order.getPayType() == PayType.BALANCE.value()) {
                /**
                 * 订单已支付并且是余额支付
                 */

            }
        }

        /**
         * 取消订单 恢复库存
         */
        cancelOrders(orders);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmOrder(List<Order> orders) {
        orderMapper.confirmOrder(orders);
        for (Order order : orders) {
            eventPublisher.publishEvent(new OrderReceiptEvent(order));
        }
    }

    @Override
    public List<Order> listOrdersDetailByOrder(Order order, Date startTime, Date endTime) {
        return orderMapper.listOrdersDetailByOrder(order, startTime, endTime);
    }

    @Override
    public IPage<Order> pageOrdersDetailByOrderParam(Page<Order> page, OrderParam orderParam) {
        page.setRecords(orderMapper.listOrdersDetailByOrderParam(new PageAdapter(page), orderParam));
        page.setTotal(orderMapper.countOrderDetail(orderParam));
        for (Order order : page.getRecords()) {
            order.setUserInfo(userService.getUserInfoById(order.getUserId()));
        }
        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrders(List<Order> orders) {
        orderMapper.deleteOrders(orders);
    }

    @Override
    public OrderCountData getOrderCount(String userId) {
        return orderMapper.getOrderCount(userId);
    }

    /**
     * 打印订单信息
     *
     * @param order
     * @return
     */
    @Override
    public String printOrder(Order order) {
        //order 信息添加附加信息
        setOrderExtraInfo(order);
        // 写入文件 供测试使用
        // testWriteObject(order);
        String print_machine_code = (String) redisTemplate.opsForHash().get(KEY_SYS_CONFIG, "print_machine_code");
        String print_order_content_template = (String) redisTemplate.opsForHash().get(KEY_SYS_CONFIG, "print_order_content_template");
        String service_tel = (String) redisTemplate.opsForHash().get(KEY_SYS_CONFIG, "service_tel");

        if (StrUtil.isBlank(print_machine_code)) {
            throw new YamiShopBindException("请在系统参数中设置系统打印机设备编码[" + print_machine_code + "]");
        }
        String printer = (String) redisTemplate.opsForHash().get("sys:printer", print_machine_code);

        if (StrUtil.isBlank(printer)) {
            throw new YamiShopBindException("请在系统打印机中设置系统打印机设备编码[" + print_machine_code + "]");
        }

        if (StrUtil.isBlank(print_order_content_template)) {
            throw new YamiShopBindException("请在系统参数中设置系统订单打印模板[" + print_order_content_template + "]");
        }

        JSONObject jsonObject = JSON.parseObject(printer);
        String accessToken = jsonObject.getString("accessToken");
        String clientId = jsonObject.getString("clientId");
        String clientSecret = jsonObject.getString("clientSecret");
        RequestMethod.init(clientId, clientSecret);
        RequestMethod instance = RequestMethod.getInstance();
        try {
            //{"error":0,"error_description":"success","timestamp":1705251051,"body":{"id":2540003905,"origin_id":"1"}}
            String printOrderContent = renderOrderPrintContentEnjoy(print_order_content_template, order, new HashMap() {{
                put("service_tel", service_tel);
            }});
            String printResult = instance.printIndex(accessToken, print_machine_code, printOrderContent, order.getOrderNumber());
            JSONObject jsonObject1 = JSON.parseObject(printResult);
            JSONObject body = jsonObject1.getJSONObject("body");
            int errorCode = jsonObject1.getInteger("error");
            if (errorCode == 0 && body != null) {
                String printId = body.getString("id");
                String origin_id = body.getString("origin_id");
                // 更新订单打印次数
                orderMapper.updateOrdersPrintTimes(Lists.newArrayList(order));
                // 添加打印日志
                Map printLogMap = new HashMap();
                printLogMap.put("print_content", printOrderContent);
                printLogMap.put("origin_id", origin_id);
                printLogMap.put("machine_code", print_machine_code);
                printLogMap.put("print_id", printId);
                printLogMap.put("order_number", order.getOrderNumber());
                List<Map> maps = Lists.newArrayList(printLogMap);
                commonMapper.addPrinterLogs(maps);
            }
            return printResult;
        } catch (Exception e) {
            e.printStackTrace();
            throw new YamiShopBindException("打印订单出错，订单编号[" + order.getOrderNumber() + "]，错误信息[" + e.getLocalizedMessage() + "]");
        }
    }

    /**
     * 设置订单附加信息
     *
     * @param order
     */
    @Override
    public void setOrderExtraInfo(Order order) {
        //订单商品信息
        List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderNumber(order.getOrderNumber());
        order.setOrderItems(orderItems);
        //订单收货地址
        UserAddrOrder userAddrOrder = userAddrOrderService.getById(order.getAddrOrderId());
        order.setUserAddrOrder(userAddrOrder);
        //订单用户信息
        UserInfoDto userInfoDto = userService.getUserInfoById(order.getUserId());
        order.setUserInfo(userInfoDto);
    }

    /**
     * 订单发货
     *
     * @param order
     * @param dvyId
     * @param dvyFlowId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void orderDelivery(Order order, Long dvyId, String dvyFlowId, Long deliveryUserId) {
        /**
         * 更新订单发货状态和物流信息
         */
        Order orderUpdate = new Order();
        orderUpdate.setOrderId(order.getOrderId());
        orderUpdate.setDvyId(dvyId);
        orderUpdate.setDvyFlowId(dvyFlowId);
        orderUpdate.setDvyTime(new Date());
        orderUpdate.setStatus(OrderStatus.CONSIGNMENT.value());
        //orderUpdate.setUserId(order.getUserId());
        updateById(orderUpdate);
        //更新订单发货状态
        // 发送用户发货通知
        // Map<String, String> params = new HashMap<>(16);
        // params.put("orderNumber", order.getOrderNumber());
        //		Delivery delivery = deliveryMapper.selectById(order.getDvyId());
        //		params.put("dvyName", delivery.getDvyName());
        //		params.put("dvyFlowId", order.getDvyFlowId());
        //		smsLogService.sendSms(SmsType.NOTIFY_DVY, order.getUserId(), order.getMobile(), params);
        // 如果是商家自配送 添加物流订单
        if (orderUpdate.getDvyId() == 13) {

            log.debug("商品由商家配送 {}", orderUpdate.getDvyId());
            DeliveryOrder deliveryOrder = new DeliveryOrder();
            deliveryOrder.setOrderNumber(order.getOrderNumber());
            deliveryOrder.setCreateTime(orderUpdate.getDvyTime());
            deliveryOrder.setExpressNumber(orderUpdate.getDvyFlowId());
            //物流状态0初始创建1已分配配送员2运送中3已送达
            deliveryOrder.setExpressStatus(0);
            deliveryOrder.setAddOrderId(order.getAddrOrderId());

            if (deliveryUserId != null) {
                //添加商家自配送运单中派送员信息
                DeliveryUser deliveryUser = deliveryUserService.getById(deliveryUserId);
                deliveryOrder.setExpressPerson(deliveryUser.getUserName());
                deliveryOrder.setExpressPhone(deliveryUser.getUserPhone());
                deliveryOrder.setExpressStatus(1);
                DeliveryOrderRoute deliveryOrderRoute = new DeliveryOrderRoute();

                deliveryOrderRoute.setCreateTime(orderUpdate.getDvyTime());
                deliveryOrderRoute.setOrderNumber(order.getOrderNumber());
                deliveryOrderRoute.setExpressNumber(orderUpdate.getDvyFlowId());
                deliveryOrderRoute.setAddOrderId(order.getAddrOrderId());
                deliveryOrderRoute.setInfo("订单" + "分配派送员 " + deliveryUser.getUserName() + " 联系电话" + deliveryUser.getUserPhone());
                //写入物流订单路由
                deliveryOrderRouteService.save(deliveryOrderRoute);
            }
            // 写入物流订单
            deliveryOrderService.save(deliveryOrder);
            eventPublisher.publishEvent(new OrderDeliveryEvent(order));
        }
    }

    /**
     * 针对某个订单退款
     *
     * @param order
     * @param refundForm
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refundOrder(Order order, OrderRefundParam refundForm) {
        Date now = new Date();
        if (Objects.equals(order.getStatus(), OrderStatus.CLOSE.value()) || order.getStatus() < OrderStatus.PADYED.value()) {
            throw new YamiShopBindException("订单" + order.getOrderNumber() + " 状态不正确，没有付款，无法申请退款");
        }
        if (order.getIsPayed() != 1) {
            throw new YamiShopBindException("订单  " + order.getOrderNumber() + " 没有付款，无法申请退款");
        }
        /**
         * 订单已支付并且是微信支付 或者余额支付
         */
        if (order.getPayType() == PayType.WECHATPAY.value() || order.getPayType() == PayType.BALANCE.value()) {
            OrderSettlement settlement = orderSettlementService.getOne(new LambdaQueryWrapper<OrderSettlement>().eq(OrderSettlement::getOrderNumber, order.getOrderNumber()));

            /**
             * 写入退款订单
             */
            OrderRefund orderRefund = new OrderRefund();
            orderRefund.setShopId(order.getShopId());
            orderRefund.setOrderId(order.getOrderId());
            orderRefund.setOrderNumber(order.getOrderNumber());
            orderRefund.setOrderAmount(order.getActualTotal());
            orderRefund.setOrderItemId(0L);
            orderRefund.setUserId(order.getUserId());
            orderRefund.setShopId(order.getShopId());

            orderRefund.setOrderPayNo(settlement.getPayNo());
            orderRefund.setBizPayNo(settlement.getBizPayNo());
            orderRefund.setPayType(settlement.getPayType());
            orderRefund.setPayTypeName(settlement.getPayTypeName());
            orderRefund.setRefundAmount(settlement.getPayAmount());
            // 平台自己的退款
            String outRefundNo = String.valueOf(snowflake.nextId());
            orderRefund.setOutRefundNo(outRefundNo);

            //申请类型:1,仅退款,2退款退货
            orderRefund.setApplyType(2);
            // 处理状态:1为待审核,2为同意,3为不同意
            orderRefund.setRefundSts(2);
            //处理退款状态: 0:退款处理中 1:退款成功 -1:退款失败
            orderRefund.setReturnMoneySts(0);
            orderRefund.setApplyTime(now);
            orderRefund.setBuyerMsg("卖家操作退款");
            // 退款操作
            orderRefund.setSellerMsg(refundForm.getSellerMsg());
            orderRefund.setApplyType(refundForm.getApplyType());
            orderRefund.setRefundMsg(refundForm.getRefundMsg());
            orderRefund.setRefundReason(refundForm.getRefundReason());
            orderRefundService.save(orderRefund);

            /**
             * 更新订单退款状态
             *
             */
            Order upOrder = new Order();
            //0:默认,1:在处理,2:处理完成
            upOrder.setRefundSts(1);
            //订单关闭原因 1-超时未支付 2-退款关闭 4-买家取消 15-已通过货到付款交易
            upOrder.setCloseType(2);
            upOrder.setStatus(6);
            upOrder.setCancelTime(now);
            upOrder.setOrderId(order.getOrderId());
            updateById(upOrder);

            /**
             * 商家操作 直接同意退款
             */
            orderRefundService.refundAccept(orderRefund);
        }

        /**
         * 取消订单 恢复库存
         */
        cancelOrders(Arrays.asList(order));
    }

    /**
     * @param order
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void orderArrive(Order order, DeliveryArriveParam deliveryArriveParam) {
        Date now = new Date();
        /**
         * 更新订单发货状态和物流信息
         */
        /*Order orderUpdate = new Order();
        orderUpdate.setOrderId(order.getOrderId());
        orderUpdate.setStatus(OrderStatus.SUCCESS.value());
        orderUpdate.setFinallyTime(now);
        orderUpdate.setUpdateTime(now);
        updateById(orderUpdate);
        log.debug("确认送达，更新订单状态成已完成");*/
        // 如果是商家自配送 添加物流订单
        if (deliveryArriveParam.getDvyId() == 13) {
            log.debug("商品由商家配送 配送商家编号 {}", deliveryArriveParam.getDvyId());
            boolean save = false;
            DeliveryOrder deliveryOrder1 = deliveryOrderService.getOne(new LambdaQueryWrapper<DeliveryOrder>().eq(DeliveryOrder::getOrderNumber, order.getOrderNumber()));
            if (deliveryOrder1 != null) {
                save = false;
            } else {
                log.debug("未查询到物流订单");
                save = true;
                deliveryOrder1 = new DeliveryOrder();
                deliveryOrder1.setCreateTime(now);
                deliveryOrder1.setAddOrderId(order.getAddrOrderId());
                deliveryOrder1.setExpressNumber(deliveryArriveParam.getDvyFlowId());
            }
            deliveryOrder1.setOrderNumber(order.getOrderNumber());
            //物流状态0初始创建1已分配配送员2运送中3已送达
            deliveryOrder1.setExpressStatus(3);

            if (deliveryArriveParam.getDeliveryUserId() != null) {
                //添加商家自配送运单中派送员信息
                DeliveryUser deliveryUser = deliveryUserService.getById(deliveryArriveParam.getDeliveryUserId());
                deliveryOrder1.setExpressPerson(deliveryUser.getUserName());
                deliveryOrder1.setExpressPhone(deliveryUser.getUserPhone());
                DeliveryOrderRoute deliveryOrderRoute = new DeliveryOrderRoute();

                deliveryOrderRoute.setCreateTime(now);
                deliveryOrderRoute.setOrderNumber(order.getOrderNumber());
                deliveryOrderRoute.setExpressNumber(deliveryArriveParam.getDvyFlowId());
                deliveryOrderRoute.setAddOrderId(order.getAddrOrderId());
                deliveryOrderRoute.setInfo(" 商品订单 " + "" + deliveryArriveParam.getRemark() + " 派送员 " + deliveryUser.getUserName() + " 联系电话" + deliveryUser.getUserPhone() + " 已送达");
                //写入物流订单路由
                deliveryOrderRouteService.save(deliveryOrderRoute);
            }
            // 写入物流订单
            if (save) {
                deliveryOrderService.save(deliveryOrder1);
            } else {
                deliveryOrderService.updateById(deliveryOrder1);
            }
        }
    }

    /**
     * 根据订单数据 提取订单编号 使用逗号分割
     *
     * @param orders
     * @return
     */
    @Override
    public String getOrderNumbers(List<Order> orders) {
        //多个订单 生成多个订单编号 使用英文逗号分割
        StringBuilder orderNumbers = new StringBuilder();
        for (Order order : orders) {
            orderNumbers.append(order.getOrderNumber()).append(",");
        }
        orderNumbers.deleteCharAt(orderNumbers.length() - 1);
        return orderNumbers.toString();
    }
}
