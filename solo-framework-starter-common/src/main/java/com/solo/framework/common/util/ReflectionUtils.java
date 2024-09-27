package com.solo.framework.common.util;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ReflectUtil;
import com.solo.framework.common.enumeration.SoloFrameworkLoggingEnum;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

public class ReflectionUtils {

    private ReflectionUtils() {

    }

    /**
     * 获取字段注解属性
     * @param targetClass 目标类
     * @param fieldName 字段名称
     * @return 字段注解属性信息Map(k-属性名称,v-属性值)
     */
    public static Map<String, Object> getFieldAnnotationsAttributes(Class<?> targetClass, String fieldName) {
        Map<String, Object> attributes = MapUtil.newHashMap();
        Field field = ReflectUtil.getField(targetClass, fieldName);
        if (Objects.isNull(field)) {
            return attributes;
        }

        Annotation[] annotations = field.getAnnotations();
        try {
            for (Annotation annotation : annotations) {
                Method[] methods = annotation.annotationType().getDeclaredMethods();
                for (Method method : methods) {
                    Object value = method.invoke(annotation);
                    attributes.put(method.getName(), value);
                }
            }
        } catch (Exception e) {
            LogUtil.log("获取注解属性失败" , SoloFrameworkLoggingEnum.WARN, e);
        }
        return attributes;
    }

}
