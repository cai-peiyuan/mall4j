package com.yami.shop.test;

import com.jfinal.ext.kit.DateKit;
import com.yami.shop.bean.model.Order;
import com.yami.shop.service.impl.OrderServiceImpl;
import com.yly.print_sdk_library.RequestMethod;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 易联云打印机测试
 *
 * @author c'p'y
 */
public class YlyTest {
    public static void main(String[] args) throws Exception {
        RequestMethod.init("1095183543", "4c21f791dc2e660f3ffa891a0da94575");
        RequestMethod instance = RequestMethod.getInstance();
        //String accessToken = RequestMethod.getAccessToken();
        //System.out.println(accessToken);
        String accessToken = "10b26f4a88a54aceb1a4212ffcc5d97f";
        String machineCode = "4004866337";
        String s = instance.printerGetPrintStatus(accessToken, machineCode);
        System.out.println(s);
        String s1 = instance.printPrintInfo(accessToken, machineCode);
        System.out.println(s1);
        String s2 = instance.printerGetPrintStatus(accessToken, machineCode);
        System.out.println(s2);
        //String s3 = instance.printIndex(accessToken, machineCode, "123456789", "123");
        //System.out.println(s3);

        //String s4 = instance.expressPrintIndex(accessToken, machineCode, "123456789", "123");
        //System.out.println(s4);
        String s3 = instance.printGetVersion(accessToken, machineCode);
        System.out.println(s3);
        String content = "<FH><FB><center>袋鼠优选</center></FB></FH>\n" +
                "********************************\n" +
                "<FH>\n" +
                "配送方式：商家配送\\r\n" +
                "收件人：#(order?.userAddrOrder?.receiver)\\r\n" +
                "手机号：#(order?.userAddrOrder?.mobile)\\r\n" +
                "收货地址：#(order?.userAddrOrder?.province)#(order?.userAddrOrder?.city)#(order?.userAddrOrder?.area)#(order?.userAddrOrder?.addr)\\r\n" +
                "下单时间：#(com.jfinal.ext.kit.DateKit::toStr(order?.createTime,\"yyyy-MM-dd HH:mm:ss\"))\\r\n" +
                "订单号：#(order?.orderNumber)</FH>\\r\n" +
                "*************商品***************\\r<FW>\n" +
                "名称\t单价\t数量\t总价\n" +
                "#for(item : order.getOrderItems())\n" +
                "#(com.yly.print_sdk_library.Utils::wrapString(item.prodName+'',4))\t#(item.price)\t#('x'+item.prodCount)\t#(item.productTotalAmount)\n" +
                "#end\n" +
                // "商家备注：\\r\n" +
                "</FW>\n" +
                "................................\n" +
                "买家备注：#(order?.remarks)\n" +
                "<FH>\n" +
                "数量：#(order?.productNums)\\r" +
                "运费：#(order?.freightAmount)\\r" +
                "原价：#(order?.total)\\r" +
                "优惠：#(order?.reduceAmount)\\r" +
                "总价：#(order?.actualTotal)\\r" +
                "********************************\\r" +
                "支付金额：#(order?.actualTotal)\n" +
                "支付方式：#(com.yly.print_sdk_library.Utils::getPayType(order?.payType))\n" +
                "支付时间：#(com.jfinal.ext.kit.DateKit::toStr(order?.payTime,\"yyyy-MM-dd HH:mm:ss\"))\n" +
                "</FH>\n" +
               // "<BR>#(order?.orderNumber)</BR>\n" +
               // "<BR2>#(order?.orderNumber)</BR2>\n" +
               // "<BR3>#(order?.orderNumber)</BR3>\n" +
                "<QR>#(order?.orderNumber)</QR>\n"+
                "<FB>袋鼠优选  客服电话：#(service_tel??\"未设置客服电话\")</FB>";
        System.out.println(content);
        //String s4 = instance.printIndex(accessToken, machineCode, content, "1");
        //System.out.println(s4);
        DateKit.toStr(new Date(),DateKit.timeStampPattern);
        Order order = OrderServiceImpl.testReadObject();
        Map map = new HashMap();
        map.put("service_tel","13102523363");
        String s4 = OrderServiceImpl.renderOrderPrintContentEnjoy(content, order, map);
        s4 = instance.printIndex(accessToken, machineCode, s4, "1");
        System.out.println(s4);
        System.out.println(order);
        //{"error":0,"error_description":"success","timestamp":1705219209,"body":{"client_id":"1095183543","access_token":"10b26f4a88a54aceb1a4212ffcc5d97f","refresh_token":"d4900cc0bce74b2f9b491dd5816a71e8","machine_code":"","expires_in":2592000,"refresh_expires_in":3024000,"scope":"all"}}
    }
}
