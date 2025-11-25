package com.solo.framework.web.annotation;

import java.lang.annotation.*;

/**
 * 不打印请求日志注解
 * 可以标注在类或方法上，被标注的接口将不会打印请求日志
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoRequestLogging {
}
