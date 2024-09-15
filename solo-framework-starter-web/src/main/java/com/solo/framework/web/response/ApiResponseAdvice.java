package com.solo.framework.web.response;

import com.alibaba.fastjson2.JSON;
import com.solo.framework.web.annotation.NoApiResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(@NonNull MethodParameter methodParameter, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return ! hasNoApiResultAnnotation(methodParameter) && ! hasSwaggerRequest(methodParameter);
    }

    @Override
    public Object beforeBodyWrite(Object body, @NonNull MethodParameter methodParameter, @NonNull MediaType selectedContentType,
                                  @NonNull Class<? extends HttpMessageConverter<?>> converterType,
                                  @NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {

        // 直接返回ApiResponse类型,不做任何处理
        if (body instanceof ApiResponse) {
            return body;
        }

        // 如果返回值是String类型，Spring将选择{org.springframework.http.converter.StringHttpMessageConverter}
        // 将其统一包装为ApiResponse对象，然后手动构建为JSON字符串后返回
        // TODO：这里使用的将是JSON的默认配置，并不是
        if (body instanceof String) {
            return JSON.toJSONString(ApiResponse.success(body));
        }

        // 其它类型: 包装为ApiResponse统一返回结果
        return ApiResponse.success(body);
    }

    private boolean hasNoApiResultAnnotation(MethodParameter methodParameter) {
        if (methodParameter.hasMethodAnnotation(NoApiResponse.class)) {
            return true;
        }

        Class<?> beanType = methodParameter.getContainingClass();
        return beanType.isAnnotationPresent(NoApiResponse.class);
    }

    private boolean hasSwaggerRequest(@NonNull MethodParameter methodParameter) {
        return methodParameter.getContainingClass().getName().contains("springfox")
                || methodParameter.getContainingClass().getName().contains("knife4j");
    }

}
