package com.yly.print_sdk_library;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 易联云打印机调用sdk
 *
 * @author c'p'y
 */
public class RequestMethod {
    private static final RequestMethod singleton = new RequestMethod();
    public static String ClientId;

    public static String ClientSecret;
    private static String example = "{\"error\":\"20\",\"error_description\":\"success\",\"body\":\"\"}";

    public static RequestMethod getInstance() {
        return singleton;
    }

    /**
     * 初始化打印sdk
     * 需要先对RequestMethod类进行init初始化设置，配置相关易联云信息
     * RequestMethod.getInstance().init(client_id,client_secret);
     * 参数：* @param client_id 易联云颁发给开发者的应用ID
     * * @param client_secret 易联云颁发给开发者的应用密钥
     *
     * @param client_id
     * @param client_secret
     */
    public static void init(String client_id, String client_secret) {
        ClientId = client_id;
        ClientSecret = client_secret;
    }

    private static boolean CCIsNull(String client_id, String client_secret) {
        if (ClientId != null && ClientSecret != null && !ClientId.equals("") && !ClientSecret.equals("")) {
            return true;
        }
        return false;
    }

    /**
     * 通过开发者应用参数获取accessToken
     *
     * @return
     * @throws Exception
     */
    public static String getAccessToken() throws Exception {
        if (CCIsNull(ClientId, ClientSecret)) {
            String timestamp = Utils.getTimestamp();
            String signMD5 = ClientId + timestamp + ClientSecret;
            String sign = Utils.getMD5Str(signMD5);
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("client_id", ClientId);
            paramMap.put("grant_type", "client_credentials");
            paramMap.put("sign", sign);
            paramMap.put("scope", "all");
            paramMap.put("timestamp", timestamp);
            paramMap.put("id", UUID.randomUUID().toString());
            return HttpRequest.sendPost(UtilUrl.freeType, paramMap);
        }
        return example;
    }

    public static String picturePrintIndex(String access_token, String machine_code, String picture_url, String origin_id) throws Exception {
        if (CCIsNull(ClientId, ClientSecret)) {
            String timestamp = Utils.getTimestamp();
            String signMD5 = ClientId + timestamp + ClientSecret;
            String sign = Utils.getMD5Str(signMD5);
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("access_token", access_token);
            paramMap.put("machine_code", machine_code);
            paramMap.put("picture_url", URLEncoder.encode(picture_url, "UTF-8"));
            paramMap.put("origin_id", origin_id);
            paramMap.put("client_id", ClientId);
            paramMap.put("timestamp", timestamp);
            paramMap.put("id", UUID.randomUUID().toString());
            paramMap.put("sign", sign);
            return HttpRequest.sendPost(UtilUrl.picturePrintIndex, paramMap);
        }
        return example;
    }

    /**
     * RequestMethod.getInstance().getCodeOpen(String redirect_uri);
     * 参数：redirect_uri 开发者自身的回调地址 (需要urlencode)
     *
     * @param redirect_uri
     * @return
     */
    public String getCodeOpen(String redirect_uri) {
        if (ClientId != null && !ClientId.equals("")) {
            return UtilUrl.openType + "?response_type=code&client_id=" + ClientId + "&redirect_uri=" + redirect_uri + "&state=1";
        }
        return example;
    }

    public String getOpenAccessToken(String code) throws Exception {
        if (CCIsNull(ClientId, ClientSecret)) {
            String timestamp = Utils.getTimestamp();
            String signMD5 = ClientId + timestamp + ClientSecret;
            String sign = Utils.getMD5Str(signMD5);
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("client_id", ClientId);
            paramMap.put("grant_type", "authorization_code");
            paramMap.put("sign", sign);
            paramMap.put("code", code);
            paramMap.put("scope", "all");
            paramMap.put("timestamp", timestamp);
            paramMap.put("id", UUID.randomUUID().toString());
            return HttpRequest.sendPost(UtilUrl.freeType, paramMap);
        }
        return example;
    }

    /**
     * Refresh token更新Access Token
     * RequestMethod.getInstance().getRefreshAccessToken(String refresh_token);
     * 参数：* @param refresh_token 更新access_token所需，有效时间35天
     *
     * @param refresh_token
     * @return
     * @throws Exception
     */
    public String getRefreshAccessToken(String refresh_token) throws Exception {
        if (CCIsNull(ClientId, ClientSecret)) {
            String timestamp = Utils.getTimestamp();
            String signMD5 = ClientId + timestamp + ClientSecret;
            String sign = Utils.getMD5Str(signMD5);
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("refresh_token", refresh_token);
            paramMap.put("grant_type", "refresh_token");
            paramMap.put("scope", "all");
            paramMap.put("client_id", ClientId);
            paramMap.put("timestamp", timestamp);
            paramMap.put("id", UUID.randomUUID().toString());
            paramMap.put("sign", sign);
            return HttpRequest.sendPost(UtilUrl.freeType, paramMap);
        }
        return example;
    }

    /**
     * 添加打印机
     * RequestMethod.getInstance().addPrinter( machine_code, msign, access_token);
     * 参数：* @param machine_code 易联云打印机终端号
     * * @param msign 易联云打印机终端密钥
     * * @param access_token 授权的token 必要参数，有效时间35天
     *
     * @param machine_code
     * @param msign
     * @param access_token
     * @return
     * @throws Exception
     */
    public String addPrinter(String machine_code, String msign, String access_token) throws Exception {
        if (CCIsNull(ClientId, ClientSecret)) {
            String timestamp = Utils.getTimestamp();
            String signMD5 = ClientId + timestamp + ClientSecret;
            String sign = Utils.getMD5Str(signMD5);
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("machine_code", machine_code);
            paramMap.put("msign", msign);
            paramMap.put("access_token", access_token);
            paramMap.put("client_id", ClientId);
            paramMap.put("timestamp", timestamp);
            paramMap.put("id", UUID.randomUUID().toString());
            paramMap.put("sign", sign);
            return HttpRequest.sendPost(UtilUrl.addPrinter, paramMap);
        }
        return example;
    }

    /**
     * 添加打印机
     *
     * @param machine_code
     * @param msign
     * @param access_token
     * @param phone
     * @param print_name
     * @return
     * @throws Exception
     */
    public String addPrinter(String machine_code, String msign, String access_token, String phone, String print_name) throws Exception {
        if (CCIsNull(ClientId, ClientSecret)) {
            String timestamp = Utils.getTimestamp();
            String signMD5 = ClientId + timestamp + ClientSecret;
            String sign = Utils.getMD5Str(signMD5);
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("machine_code", machine_code);
            paramMap.put("msign", msign);
            paramMap.put("access_token", access_token);
            paramMap.put("phone", phone);
            paramMap.put("print_name", print_name);
            paramMap.put("client_id", ClientId);
            paramMap.put("timestamp", timestamp);
            paramMap.put("id", UUID.randomUUID().toString());
            paramMap.put("sign", sign);
            return HttpRequest.sendPost(UtilUrl.addPrinter, paramMap);
        }
        return example;
    }

    public String scanCodeModel(String machine_code, String qr_key) throws Exception {
        if (CCIsNull(ClientId, ClientSecret)) {
            String timestamp = Utils.getTimestamp();
            String signMD5 = ClientId + timestamp + ClientSecret;
            String sign = Utils.getMD5Str(signMD5);
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("machine_code", machine_code);
            paramMap.put("qr_key", qr_key);
            paramMap.put("scope", "all");
            paramMap.put("client_id", ClientId);
            paramMap.put("timestamp", timestamp);
            paramMap.put("id", UUID.randomUUID().toString());
            paramMap.put("sign", sign);
            return HttpRequest.sendPost(UtilUrl.scanCodeModel, paramMap);
        }
        return example;
    }

    public String scanCodeModel_msign(String machine_code, String msign) throws Exception {
        if (CCIsNull(ClientId, ClientSecret)) {
            String timestamp = Utils.getTimestamp();
            String signMD5 = ClientId + timestamp + ClientSecret;
            String sign = Utils.getMD5Str(signMD5);
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("machine_code", machine_code);
            paramMap.put("msign", msign);
            paramMap.put("scope", "all");
            paramMap.put("client_id", ClientId);
            paramMap.put("timestamp", timestamp);
            paramMap.put("id", UUID.randomUUID().toString());
            paramMap.put("sign", sign);
            return HttpRequest.sendPost(UtilUrl.scanCodeModel, paramMap);
        }
        return example;
    }

    /**
     * 文本打印
     * RequestMethod.getInstance().printIndex(String access_token,String machine_code,String content,String origin_id)；
     * 参数：* @param access_token 授权的token 必要参数
     * * @param machine_code 易联云打印机终端号
     * * @param content 打印内容(需要urlencode)，排版指令详见打印机指令
     * * @param origin_id 商户系统内部订单号，要求32个字符内，只能是数字、大小写字母 ，且在同一个client_id下唯一。详见商户订单号
     *
     * @param access_token
     * @param machine_code
     * @param content
     * @param origin_id
     * @return
     * @throws Exception
     */
    public String printIndex(String access_token, String machine_code, String content, String origin_id) throws Exception {
        if (CCIsNull(ClientId, ClientSecret)) {
            String timestamp = Utils.getTimestamp();
            String signMD5 = ClientId + timestamp + ClientSecret;
            String sign = Utils.getMD5Str(signMD5);
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("access_token", access_token);
            paramMap.put("machine_code", machine_code);
            paramMap.put("content", content);
            paramMap.put("origin_id", origin_id);
            paramMap.put("client_id", ClientId);
            paramMap.put("timestamp", timestamp);
            paramMap.put("id", UUID.randomUUID().toString());
            paramMap.put("sign", sign);
            return HttpRequest.sendPost(UtilUrl.printIndex, paramMap);
        }
        return example;
    }

    public String expressPrintIndex(String access_token, String machine_code, String content, String origin_id) throws Exception {
        if (CCIsNull(ClientId, ClientSecret)) {
            String timestamp = Utils.getTimestamp();
            String signMD5 = ClientId + timestamp + ClientSecret;
            String sign = Utils.getMD5Str(signMD5);
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("access_token", access_token);
            paramMap.put("machine_code", machine_code);
            paramMap.put("content", content);
            paramMap.put("origin_id", origin_id);
            paramMap.put("client_id", ClientId);
            paramMap.put("timestamp", timestamp);
            paramMap.put("id", UUID.randomUUID().toString());
            paramMap.put("sign", sign);
            return HttpRequest.sendPost(UtilUrl.expressPrintIndex, paramMap);
        }
        return example;
    }

    public String printerSetVoice(String access_token, String machine_code, String content, String is_file, String aid, String origin_id) throws Exception {
        if (CCIsNull(ClientId, ClientSecret)) {
            String timestamp = Utils.getTimestamp();
            String signMD5 = ClientId + timestamp + ClientSecret;
            String sign = Utils.getMD5Str(signMD5);
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("access_token", access_token);
            paramMap.put("machine_code", machine_code);
            paramMap.put("content", content);
            paramMap.put("is_file", is_file);
            paramMap.put("aid", aid);
            paramMap.put("origin_id", origin_id);
            paramMap.put("client_id", ClientId);
            paramMap.put("timestamp", timestamp);
            paramMap.put("id", UUID.randomUUID().toString());
            paramMap.put("sign", sign);
            return HttpRequest.sendPost(UtilUrl.printerSetVoice, paramMap);
        }
        return example;
    }

    public String printerDeleteVoice(String access_token, String machine_code, String aid, String origin_id) throws Exception {
        if (CCIsNull(ClientId, ClientSecret)) {
            String timestamp = Utils.getTimestamp();
            String signMD5 = ClientId + timestamp + ClientSecret;
            String sign = Utils.getMD5Str(signMD5);
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("access_token", access_token);
            paramMap.put("machine_code", machine_code);
            paramMap.put("aid", aid);
            paramMap.put("origin_id", origin_id);
            paramMap.put("client_id", ClientId);
            paramMap.put("timestamp", timestamp);
            paramMap.put("id", UUID.randomUUID().toString());
            paramMap.put("sign", sign);
            return HttpRequest.sendPost(UtilUrl.printerDeleteVoice, paramMap);
        }
        return example;
    }

    public String printerDeletePrinter(String access_token, String machine_code) throws Exception {
        if (CCIsNull(ClientId, ClientSecret)) {
            String timestamp = Utils.getTimestamp();
            String signMD5 = ClientId + timestamp + ClientSecret;
            String sign = Utils.getMD5Str(signMD5);
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("access_token", access_token);
            paramMap.put("machine_code", machine_code);
            paramMap.put("client_id", ClientId);
            paramMap.put("timestamp", timestamp);
            paramMap.put("id", UUID.randomUUID().toString());
            paramMap.put("sign", sign);
            return HttpRequest.sendPost(UtilUrl.printerDeletePrinter, paramMap);
        }
        return example;
    }

    public String printMenuAddPrintMenu(String access_token, String machine_code, String content) throws Exception {
        if (CCIsNull(ClientId, ClientSecret)) {
            String timestamp = Utils.getTimestamp();
            String signMD5 = ClientId + timestamp + ClientSecret;
            String sign = Utils.getMD5Str(signMD5);
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("access_token", access_token);
            paramMap.put("machine_code", machine_code);
            paramMap.put("content", content);
            paramMap.put("client_id", ClientId);
            paramMap.put("timestamp", timestamp);
            paramMap.put("id", UUID.randomUUID().toString());
            paramMap.put("sign", sign);
            return HttpRequest.sendPost(UtilUrl.printMenuAddPrintMenu, paramMap);
        }
        return example;
    }

    public String printShutdownRestart(String access_token, String machine_code, String response_type) throws Exception {
        if (CCIsNull(ClientId, ClientSecret)) {
            String timestamp = Utils.getTimestamp();
            String signMD5 = ClientId + timestamp + ClientSecret;
            String sign = Utils.getMD5Str(signMD5);
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("access_token", access_token);
            paramMap.put("machine_code", machine_code);
            paramMap.put("response_type", response_type);
            paramMap.put("client_id", ClientId);
            paramMap.put("timestamp", timestamp);
            paramMap.put("id", UUID.randomUUID().toString());
            paramMap.put("sign", sign);
            return HttpRequest.sendPost(UtilUrl.printShutdownRestart, paramMap);
        }
        return example;
    }

    public String printSetSound(String access_token, String machine_code, String response_type, String voice) throws Exception {
        if (CCIsNull(ClientId, ClientSecret)) {
            String timestamp = Utils.getTimestamp();
            String signMD5 = ClientId + timestamp + ClientSecret;
            String sign = Utils.getMD5Str(signMD5);
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("access_token", access_token);
            paramMap.put("machine_code", machine_code);
            paramMap.put("response_type", response_type);
            paramMap.put("voice", voice);
            paramMap.put("client_id", ClientId);
            paramMap.put("timestamp", timestamp);
            paramMap.put("id", UUID.randomUUID().toString());
            paramMap.put("sign", sign);
            return HttpRequest.sendPost(UtilUrl.printSetSound, paramMap);
        }
        return example;
    }

    public String printPrintInfo(String access_token, String machine_code) throws Exception {
        if (CCIsNull(ClientId, ClientSecret)) {
            String timestamp = Utils.getTimestamp();
            String signMD5 = ClientId + timestamp + ClientSecret;
            String sign = Utils.getMD5Str(signMD5);
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("access_token", access_token);
            paramMap.put("machine_code", machine_code);
            paramMap.put("client_id", ClientId);
            paramMap.put("timestamp", timestamp);
            paramMap.put("id", UUID.randomUUID().toString());
            paramMap.put("sign", sign);
            return HttpRequest.sendPost(UtilUrl.printPrintInfo, paramMap);
        }
        return example;
    }

    public String printGetVersion(String access_token, String machine_code) throws Exception {
        if (CCIsNull(ClientId, ClientSecret)) {
            String timestamp = Utils.getTimestamp();
            String signMD5 = ClientId + timestamp + ClientSecret;
            String sign = Utils.getMD5Str(signMD5);
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("access_token", access_token);
            paramMap.put("machine_code", machine_code);
            paramMap.put("client_id", ClientId);
            paramMap.put("timestamp", timestamp);
            paramMap.put("id", UUID.randomUUID().toString());
            paramMap.put("sign", sign);
            return HttpRequest.sendPost(UtilUrl.printGetVersion, paramMap);
        }
        return example;
    }

    public String printCancelAll(String access_token, String machine_code) throws Exception {
        if (CCIsNull(ClientId, ClientSecret)) {
            String timestamp = Utils.getTimestamp();
            String signMD5 = ClientId + timestamp + ClientSecret;
            String sign = Utils.getMD5Str(signMD5);
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("access_token", access_token);
            paramMap.put("machine_code", machine_code);
            paramMap.put("client_id", ClientId);
            paramMap.put("timestamp", timestamp);
            paramMap.put("id", UUID.randomUUID().toString());
            paramMap.put("sign", sign);
            return HttpRequest.sendPost(UtilUrl.printCancelAll, paramMap);
        }
        return example;
    }

    public String printCancelOne(String access_token, String machine_code, String order_id) throws Exception {
        if (CCIsNull(ClientId, ClientSecret)) {
            String timestamp = Utils.getTimestamp();
            String signMD5 = ClientId + timestamp + ClientSecret;
            String sign = Utils.getMD5Str(signMD5);
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("access_token", access_token);
            paramMap.put("machine_code", machine_code);
            paramMap.put("order_id", order_id);
            paramMap.put("client_id", ClientId);
            paramMap.put("timestamp", timestamp);
            paramMap.put("id", UUID.randomUUID().toString());
            paramMap.put("sign", sign);
            return HttpRequest.sendPost(UtilUrl.printCancelOne, paramMap);
        }
        return example;
    }

    public String printSetIcon(String access_token, String machine_code, String img_url) throws Exception {
        if (CCIsNull(ClientId, ClientSecret)) {
            String timestamp = Utils.getTimestamp();
            String signMD5 = ClientId + timestamp + ClientSecret;
            String sign = Utils.getMD5Str(signMD5);
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("access_token", access_token);
            paramMap.put("machine_code", machine_code);
            paramMap.put("img_url", img_url);
            paramMap.put("client_id", ClientId);
            paramMap.put("timestamp", timestamp);
            paramMap.put("id", UUID.randomUUID().toString());
            paramMap.put("sign", sign);
            return HttpRequest.sendPost(UtilUrl.printSetIcon, paramMap);
        }
        return example;
    }

    public String printDeleteIcon(String access_token, String machine_code) throws Exception {
        if (CCIsNull(ClientId, ClientSecret)) {
            String timestamp = Utils.getTimestamp();
            String signMD5 = ClientId + timestamp + ClientSecret;
            String sign = Utils.getMD5Str(signMD5);
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("access_token", access_token);
            paramMap.put("machine_code", machine_code);
            paramMap.put("client_id", ClientId);
            paramMap.put("timestamp", timestamp);
            paramMap.put("id", UUID.randomUUID().toString());
            paramMap.put("sign", sign);
            return HttpRequest.sendPost(UtilUrl.printDeleteIcon, paramMap);
        }
        return example;
    }

    public String printBtnPrint(String access_token, String machine_code, String response_type) throws Exception {
        if (CCIsNull(ClientId, ClientSecret)) {
            String timestamp = Utils.getTimestamp();
            String signMD5 = ClientId + timestamp + ClientSecret;
            String sign = Utils.getMD5Str(signMD5);
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("access_token", access_token);
            paramMap.put("machine_code", machine_code);
            paramMap.put("response_type", response_type);
            paramMap.put("client_id", ClientId);
            paramMap.put("timestamp", timestamp);
            paramMap.put("id", UUID.randomUUID().toString());
            paramMap.put("sign", sign);
            return HttpRequest.sendPost(UtilUrl.printBtnPrint, paramMap);
        }
        return example;
    }

    public String printGetOrder(String access_token, String machine_code, String response_type) throws Exception {
        if (CCIsNull(ClientId, ClientSecret)) {
            String timestamp = Utils.getTimestamp();
            String signMD5 = ClientId + timestamp + ClientSecret;
            String sign = Utils.getMD5Str(signMD5);
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("access_token", access_token);
            paramMap.put("machine_code", machine_code);
            paramMap.put("response_type", response_type);
            paramMap.put("client_id", ClientId);
            paramMap.put("timestamp", timestamp);
            paramMap.put("id", UUID.randomUUID().toString());
            paramMap.put("sign", sign);
            return HttpRequest.sendPost(UtilUrl.printGetOrder, paramMap);
        }
        return example;
    }

    public String oauthSetPushUrl(String access_token, String machine_code, String cmd, String url, String status) throws Exception {
        if (CCIsNull(ClientId, ClientSecret)) {
            String timestamp = Utils.getTimestamp();
            String signMD5 = ClientId + timestamp + ClientSecret;
            String sign = Utils.getMD5Str(signMD5);
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("access_token", access_token);
            paramMap.put("machine_code", machine_code);
            paramMap.put("cmd", cmd);
            paramMap.put("url", url);
            paramMap.put("status", status);
            paramMap.put("client_id", ClientId);
            paramMap.put("timestamp", timestamp);
            paramMap.put("id", UUID.randomUUID().toString());
            paramMap.put("sign", sign);
            return HttpRequest.sendPost(UtilUrl.oauthSetPushUrl, paramMap);
        }
        return example;
    }

    public String printerGetOrderStatus(String access_token, String machine_code, String order_id) throws Exception {
        if (CCIsNull(ClientId, ClientSecret)) {
            String timestamp = Utils.getTimestamp();
            String signMD5 = ClientId + timestamp + ClientSecret;
            String sign = Utils.getMD5Str(signMD5);
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("access_token", access_token);
            paramMap.put("machine_code", machine_code);
            paramMap.put("order_id", order_id);
            paramMap.put("client_id", ClientId);
            paramMap.put("timestamp", timestamp);
            paramMap.put("id", UUID.randomUUID().toString());
            paramMap.put("sign", sign);
            return HttpRequest.sendPost(UtilUrl.printerGetOrderStatus, paramMap);
        }
        return example;
    }

    public String printerGetOrderPagingList(String access_token, String machine_code, String page_index, String page_size) throws Exception {
        if (CCIsNull(ClientId, ClientSecret)) {
            String timestamp = Utils.getTimestamp();
            String signMD5 = ClientId + timestamp + ClientSecret;
            String sign = Utils.getMD5Str(signMD5);
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("access_token", access_token);
            paramMap.put("machine_code", machine_code);
            paramMap.put("page_index", page_index);
            paramMap.put("page_size", page_size);
            paramMap.put("client_id", ClientId);
            paramMap.put("timestamp", timestamp);
            paramMap.put("id", UUID.randomUUID().toString());
            paramMap.put("sign", sign);
            return HttpRequest.sendPost(UtilUrl.printerGetOrderPagingList, paramMap);
        }
        return example;
    }

    public String printerGetPrintStatus(String access_token, String machine_code) throws Exception {
        if (CCIsNull(ClientId, ClientSecret)) {
            String timestamp = Utils.getTimestamp();
            String signMD5 = ClientId + timestamp + ClientSecret;
            String sign = Utils.getMD5Str(signMD5);
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("access_token", access_token);
            paramMap.put("machine_code", machine_code);
            paramMap.put("client_id", ClientId);
            paramMap.put("timestamp", timestamp);
            paramMap.put("id", UUID.randomUUID().toString());
            paramMap.put("sign", sign);
            return HttpRequest.sendPost(UtilUrl.printerGetPrintStatus, paramMap);
        }
        return example;
    }
}
