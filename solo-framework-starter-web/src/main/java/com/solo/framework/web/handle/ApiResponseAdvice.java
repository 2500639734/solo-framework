package com.solo.framework.web.handle;

import com.alibaba.fastjson2.JSON;
import com.solo.framework.web.annotation.NoApiResponse;
import com.solo.framework.web.enums.IErrorCodeEnums;
import com.solo.framework.web.exception.IErrorException;
import com.solo.framework.web.exception.IErrorHttpNoFoundException;
import com.solo.framework.web.response.ApiResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Map;
import java.util.Objects;

/**
 * 全局返参处理 / 全局异常处理
 */
@RestControllerAdvice
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {

    private static final String REQUEST_NOT_FOUND_STATUS= "status";
    private static final String REQUEST_NOT_FOUND_ERROR = "error";
    private static final String REQUEST_NOT_FOUND_PATH = "path";
    private static final String REQUEST_NOT_FOUND_MESSAGE = "Not Found";

    @Override
    public boolean supports(@NonNull MethodParameter methodParameter, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return ! hasNoApiResultAnnotation(methodParameter) && ! hasSwaggerRequest(methodParameter);
    }

    @Override
    public Object beforeBodyWrite(Object body, @NonNull MethodParameter methodParameter, @NonNull MediaType selectedContentType,
                                  @NonNull Class<? extends HttpMessageConverter<?>> converterType,
                                  @NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {

        // 检查请求是否404
        checkRequestHttp404(body);

        // 直接返回ApiResponse类型,不做任何处理
        if (body instanceof ApiResponse) {
            return body;
        }

        // 如果返回值是String类型，Spring将选择{org.springframework.http.converter.StringHttpMessageConverter}
        // 将其统一包装为ApiResponse对象，然后手动构建为JSON字符串后返回
        if (body instanceof String) {
            return JSON.toJSONString(ApiResponse.success(body));
        }

        // 其它类型: 包装为ApiResponse统一返回结果
        return ApiResponse.success(body);
    }

    @ExceptionHandler(IErrorHttpNoFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoHandlerFoundException(IErrorHttpNoFoundException ex) {
        ApiResponse<Void> apiErrorResponse = ApiResponse.error(IErrorCodeEnums.ERROR_REQUEST_URI_INVALID, null, ex);
        // TODO：log
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        ApiResponse<Void> apiErrorResponse = ApiResponse.error(IErrorCodeEnums.ERROR_REQUEST_PARAMS_FORMAT_INVALID, null, ex);
        // TODO：log
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IErrorException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(IErrorException ex) {
        ApiResponse<Void> apiErrorResponse = ApiResponse.error(ex.getErrorCode(), ex.getMessage(), null, ex);
        // TODO：log
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /*********************************** private method start *************************************/

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

    private static void checkRequestHttp404(Object body) {
        if (body instanceof Map) {
            Map<?, ?> responseMap = (Map<?, ?>) body;
            Object statusObj = responseMap.get(REQUEST_NOT_FOUND_STATUS);
            Object errorObj = responseMap.get(REQUEST_NOT_FOUND_ERROR);
            Object pathObj = responseMap.get(REQUEST_NOT_FOUND_PATH);
            if (Integer.valueOf(HttpStatus.NOT_FOUND.value()).equals(statusObj) && REQUEST_NOT_FOUND_MESSAGE.equals(errorObj)) {
                throw new IErrorHttpNoFoundException(IErrorCodeEnums.ERROR_REQUEST_URI_INVALID.getResultCode(),
                        Objects.toString(errorObj, "") + ": " + Objects.toString(pathObj, ""));
            }
        }
    }

}
