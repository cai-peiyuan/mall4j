package com.yami.shop.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
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
import com.yami.shop.bean.event.OrderCancelEvent;
import com.yami.shop.bean.event.OrderDeliveryEvent;
import com.yami.shop.bean.event.OrderReceiptEvent;
import com.yami.shop.bean.event.OrderSubmitEvent;
import com.yami.shop.bean.model.DeliveryOrder;
import com.yami.shop.bean.model.Order;
import com.yami.shop.bean.model.OrderItem;
import com.yami.shop.bean.model.UserAddrOrder;
import com.yami.shop.bean.param.OrderParam;
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
    private final UserService userService;
    private final OrderMapper orderMapper;
    private final CommonMapper commonMapper;
    private final SkuMapper skuMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final UserAddrOrderService userAddrOrderService;
    private final OrderItemService orderItemService;
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

    @Override
    @CacheEvict(cacheNames = "ConfirmOrderCache", key = "#userId")
    public void removeConfirmOrderCache(String userId) {
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Order> submit(String userId, ShopCartOrderMergerDto mergerOrder) {
        List<Order> orderList = new ArrayList<>();
        // 通过事务提交订单
        eventPublisher.publishEvent(new OrderSubmitEvent(mergerOrder, orderList));

        // 插入订单
        saveBatch(orderList);
        List<OrderItem> orderItems = orderList.stream().flatMap(order -> order.getOrderItems().stream()).collect(Collectors.toList());
        // 插入订单项，返回主键
        orderItemMapper.insertBatch(orderItems);
        return orderList;
    }

    @Override
    public List<Order> listOrderAndOrderItems(Integer orderStatus, DateTime lessThanUpdateTime) {
        return orderMapper.listOrderAndOrderItems(orderStatus, lessThanUpdateTime);
    }

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
        productMapper.returnStock(prodCollect);

        allOrderItems.stream().collect(Collectors.groupingBy(OrderItem::getSkuId)).forEach((skuId, orderItems) -> {
            int prodTotalNum = orderItems.stream().mapToInt(OrderItem::getProdCount).sum();
            skuCollect.put(skuId, prodTotalNum);
        });
        skuMapper.returnStock(skuCollect);
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
    public void orderDelivery(Order order, Long dvyId, String dvyFlowId) {
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
            deliveryOrder.setOrderNumber(orderUpdate.getOrderNumber());
            deliveryOrder.setCreateTime(orderUpdate.getDvyTime());
            deliveryOrder.setExpressNumber(orderUpdate.getDvyFlowId());
            //物流状态0初始创建1已分配配送员2运送中3已送达
            deliveryOrder.setExpressStatus(0);
            deliveryOrder.setAddOrderId(order.getAddrOrderId());
            deliveryOrderService.save(deliveryOrder);

            eventPublisher.publishEvent(new OrderDeliveryEvent(order));
        }
    }

}
