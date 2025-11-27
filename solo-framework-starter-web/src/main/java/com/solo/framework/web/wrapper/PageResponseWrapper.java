package com.solo.framework.web.wrapper;

import com.solo.framework.common.enumeration.SoloFrameworkLoggingEnum;
import com.solo.framework.common.util.LogUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.util.Objects;

/**
 * 分页包装类: 将 MyBatis-Plus 风格 Page 对象包装为 PageResponse。
 */
@Slf4j
public class PageResponseWrapper {

    private static final String PAGE_CLASS = "com.baomidou.mybatisplus.extension.plugins.pagination.Page";
    private static final String PAGE_RESPONSE_CLASS = "com.solo.framework.mts.dto.response.PageResponse";

    private PageResponseWrapper() {
    }

    /**
     * 尝试将对象包装为 PageResponse
     *
     * @param body 可能是 Page 对象
     * @return 包装后的 PageResponse 对象，如果不是 Page 或包装失败则返回原始对象
     */
    public static Object wrapIfPage(Object body) {
        if (Objects.isNull(body)) {
            return null;
        }

        try {
            Class<?> pageClass = findPageClass(body.getClass());
            if (Objects.isNull(pageClass)) {
                return body;
            }

            Class<?> pageResponseClass = Class.forName(PAGE_RESPONSE_CLASS);
            Constructor<?> constructor = pageResponseClass.getConstructor(pageClass);
            return constructor.newInstance(body);

        } catch (Throwable e) {
            LogUtil.log("Page 转 PageResponse 失败，将返回原始对象: {}", SoloFrameworkLoggingEnum.WARN, body.getClass().getName(), e);
            return body;
        }
    }

    /**
     * 递归查找 Page 类，支持子类或代理类
     *
     * @param clazz 对象类型
     * @return Page 类对象，如果找不到返回 null
     */
    private static Class<?> findPageClass(Class<?> clazz) {
        if (Objects.isNull(clazz) || clazz == Object.class) {
            return null;
        }

        if (PAGE_CLASS.equals(clazz.getName())) {
            return clazz;
        }

        // 递归父类
        return findPageClass(clazz.getSuperclass());
    }

}
