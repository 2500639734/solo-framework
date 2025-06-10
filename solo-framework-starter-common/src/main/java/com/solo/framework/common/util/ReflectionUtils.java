package com.solo.framework.common.util;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.solo.framework.common.enumeration.SoloFrameworkLoggingEnum;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ReflectionUtils extends ReflectUtil {

    private ReflectionUtils() {

    }

    private static final String SETTER_PREFIX = "set";

    private static final String GETTER_PREFIX = "get";

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

    /**
     * 调用Getter方法.
     * 支持多级，如：对象名.对象名.方法
     */
    @SuppressWarnings("unchecked")
    public static <E> E invokeGetter(Object obj, String propertyName) {
        Object object = obj;
        for (String name : StrUtil.split(propertyName, ".")) {
            String getterMethodName = GETTER_PREFIX + StringUtils.capitalize(name);
            object = invoke(object, getterMethodName);
        }
        return (E) object;
    }

    /**
     * 调用Setter方法, 仅匹配方法名。
     * 支持多级，如：对象名.对象名.方法
     */
    public static <E> void invokeSetter(Object obj, String propertyName, E value) {
        Object object = obj;
        List<String> names = StrUtil.split(propertyName, ".");
        for (int i = 0; i < names.size(); i++) {
            if (i < names.size() - 1) {
                String getterMethodName = GETTER_PREFIX + StringUtils.capitalize(names.get(i));
                object = invoke(object, getterMethodName);
            } else {
                String setterMethodName = SETTER_PREFIX + StringUtils.capitalize(names.get(i));
                Method method = getMethodByName(object.getClass(), setterMethodName);
                invoke(object, method, value);
            }
        }
    }

}
