package com.qq.wechat.pay.test;

import com.qq.wechat.pay.WeChatPayUtil;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import com.wechat.pay.java.service.payments.jsapi.model.Amount;
import com.wechat.pay.java.service.payments.jsapi.model.Payer;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayResponse;

/**
 * JSAPI 下单为例
 * @author c'p'y
 * */
public class QuickStart {


  public static void main(String[] args) {
    // 使用自动更新平台证书的RSA配置
    // 一个商户号只能初始化一个配置，否则会因为重复的下载任务报错
    Config config =
        new RSAAutoCertificateConfig.Builder()
            .merchantId(WeChatPayUtil.merchantId)
            .privateKeyFromPath(WeChatPayUtil.privateKeyPath)
            .merchantSerialNumber(WeChatPayUtil.merchantSerialNumber)
            .apiV3Key(WeChatPayUtil.apiV3Key)
            .build();
    JsapiService service = new JsapiService.Builder().config(config).build();
    // request.setXxx(val)设置所需参数，具体参数可见Request定义
    PrepayRequest request = new PrepayRequest();
    Amount amount = new Amount();
    amount.setTotal(1);
    request.setAmount(amount);
    request.setAppid(WeChatPayUtil.appId);
    request.setMchid(WeChatPayUtil.merchantId);
    request.setDescription("测试商品标题");
    request.setNotifyUrl(WeChatPayUtil.WXPAY_NOTIFY_URL_TRANSACTION);
    request.setOutTradeNo("out_trade_no_001");
    Payer payer = new Payer();
    payer.setOpenid("o7hh869Yv8oVzVJ8UEUPorTqeMI8");
    request.setPayer(payer);
    PrepayResponse response = service.prepay(request);
    System.out.println(response.getPrepayId());
  }
}
