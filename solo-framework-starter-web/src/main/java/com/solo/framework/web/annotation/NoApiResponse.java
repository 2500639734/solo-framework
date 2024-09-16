package com.solo.framework.web.annotation;

import com.solo.framework.web.handle.ApiResponseAdvice;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记在类上: 该类的所有接口返参数据将不会被内置处理器处理
 * 标记在方法上: 该方法的所有接口返参数据不会被内置处理器处理
 *  - 内置处理器{@link ApiResponseAdvice}
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoApiResponse {

}
