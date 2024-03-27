package com.yly.print_sdk_library;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author c'p'y
 */
public class Utils {
    public static String getMD5Str(String str) {
        String re = null;
        try {
            byte[] tem = str.getBytes();
            MessageDigest md5 = MessageDigest.getInstance("md5");
            md5.reset();
            md5.update(tem);
            byte[] encrypt = md5.digest();
            StringBuilder sb = new StringBuilder();
            for (byte t : encrypt) {
                String s = Integer.toHexString(t & 0xFF);
                if (s.length() == 1) {
                    s = "0" + s;
                }
                sb.append(s);
            }
            re = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            re = "";
        }
        if (re.length() == 31) {
            return "0" + re;
        }
        return re;
    }

    public static boolean isNull(String content) {
        if (content != null && !content.equals("")) {
            return false;
        }
        return true;
    }

    public static String getTimestamp() {
        return String.valueOf(System.currentTimeMillis() / 1000L);
    }

    /**
     * 将一个长的字符串换行显示
     *
     * @param input
     * @param lineLength
     * @return
     */
    public static String wrapString(String input, int lineLength) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (i > 0 && i % lineLength == 0) {
                output.append("\n");
            }
            output.append(c);
        }
        return output.toString();
    }

    /**
     * 根据支付方式代码获取支付方式名称
     *
     * @return
     */
    public static String getPayType(int payType) {
        Map<Integer, String> payTypeMap = new HashMap() {
            {
                put(1, "微信支付");
                put(2, "支付宝");
                put(3, "储值余额");
            }
        };
        return payTypeMap.containsKey(payType) ? payTypeMap.get(payType) : "余额支付";
    }
}
