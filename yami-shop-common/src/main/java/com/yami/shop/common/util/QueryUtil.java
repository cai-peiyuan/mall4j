package com.yami.shop.common.util;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.jfinal.kit.StrKit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * sql查询工具类
 */
public class QueryUtil {

    public static void dynamicOrder(LambdaQueryWrapper queryWrapper, String sortField, String isAsc, Class clazz) {
        try {
            if (StrKit.hasBlank(isAsc, sortField)) {
                return;
            }
            // 根据字段名获取对应的排序方法
            String methodName = "ascending".equalsIgnoreCase(isAsc) ? "orderByAsc" : "orderByDesc";
            Method method = LambdaQueryWrapper.class.getMethod(methodName, LambdaQueryWrapper.class);

            // 通过反射获取实体类中对应字段的 getter 方法
            String getterName = "get" + Character.toUpperCase(sortField.charAt(0)) + sortField.substring(1);
            Method getter = clazz.getMethod(getterName);

            // 使用反射调用 LambdaQueryWrapper 的排序方法
            method.invoke(queryWrapper, getter.toGenericString());
        } catch (Exception e) {
            // 异常处理，例如打印日志或抛出自定义异常
            e.printStackTrace();
        }
    }

    /**
     * 将驼峰命名法的字符串转换为下划线命名法。
     *
     * @param camelCaseString 驼峰命名法的字符串
     * @return 下划线命名法的字符串
     */
    public static String camelToUnderline(String camelCaseString) {
        StringBuilder result = new StringBuilder();
        for (char c : camelCaseString.toCharArray()) {
            if (Character.isUpperCase(c)) {
                result.append('_').append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * 通过get参数为page查询参数设置排序方法
     *
     * @param page
     */
    public static void pageOrder(PageParam page) {
        if (StrKit.hasBlank(page.getOrder(), page.getOrderField())) {
            return;
        }
        String colum = camelToUnderline(page.getOrderField());
        page.addOrder("ascending".equalsIgnoreCase(page.getOrder()) ? OrderItem.asc(colum) : OrderItem.desc(colum));
    }
}
