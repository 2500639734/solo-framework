package com.solo.framework.web.handle;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.solo.framework.common.enumeration.SoloFrameworkLoggingEnum;
import com.solo.framework.common.function.NoArgSupplier;
import com.solo.framework.common.util.LogUtil;
import com.solo.framework.common.util.ReflectionUtils;
import com.solo.framework.core.env.SoloFrameworkRuntimeInfo;
import com.solo.framework.core.properties.web.response.SoloFrameworkWebResponseProperties;
import com.solo.framework.web.annotation.NoApiResponse;
import com.solo.framework.web.enums.ErrorCodeEnums;
import com.solo.framework.web.exception.IErrorException;
import com.solo.framework.web.exception.IErrorHttpNoFoundException;
import com.solo.framework.web.response.ApiResponse;
import com.solo.framework.web.response.ApiResponseAbstract;
import com.solo.framework.web.util.MessageUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Map;
import java.util.Objects;

/**
 * 全局返参处理 / 全局异常处理
 */
@Slf4j
@RestControllerAdvice
public class ApiResponseAdvice implements ResponseBodyAdvice<Object>, Ordered, IApiResponseAdvice {

    @Setter(onMethod_ = {@Autowired})
    private SoloFrameworkWebResponseProperties soloFrameworkWebResponseProperties;

    private static final String REQUEST_NOT_FOUND_STATUS= "status";
    private static final String REQUEST_NOT_FOUND_ERROR = "error";
    private static final String REQUEST_NOT_FOUND_PATH = "path";
    private static final String REQUEST_NOT_FOUND_MESSAGE = "Not Found";

    @Override
    public boolean supports(@NonNull MethodParameter methodParameter, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return soloFrameworkWebResponseProperties.isEnabled() && ! hasNoApiResponseAnnotation(methodParameter) && ! hasSwaggerRequest(methodParameter);
    }

    @Override
    public Object beforeBodyWrite(Object body, @NonNull MethodParameter methodParameter, @NonNull MediaType selectedContentType,
                                  @NonNull Class<? extends HttpMessageConverter<?>> converterType,
                                  @NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {

        // 检查请求是否404
        checkRequestHttp404(body);

        // 直接返回ApiResponse类型,不做任何处理
        if (body instanceof ApiResponseAbstract) {
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

    /**
     * 请求接口地址错误异常捕获
     * @param ex 请求接口地址错误异常
     * @return {@link com.solo.framework.web.response.ApiResponseAbstract<Void>}
     */
    @ExceptionHandler(IErrorHttpNoFoundException.class)
    public ResponseEntity<ApiResponseAbstract<Void>> handleNoHandlerFoundException(IErrorHttpNoFoundException ex) {
        return buildApiResponseResponseEntity(ex, ErrorCodeEnums.ERROR_REQUEST_URI_INVALID, HttpStatus.NOT_FOUND);
    }

    /**
     * 请求接口地址错误异常捕获
     * @param ex 请求接口地址错误异常
     * @return {@link com.solo.framework.web.response.ApiResponseAbstract<Void>}
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponseAbstract<Void>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        return buildApiResponseResponseEntity(ex, ErrorCodeEnums.ERROR_REQUEST_WAY_INVALID, HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * 请求参数值校验错误异常捕获(Object类型参数校验)
     * @param ex 请求接口参数值校验异常
     * @return {@link com.solo.framework.web.response.ApiResponseAbstract<Void>}
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseAbstract<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        ObjectError error = ex.getBindingResult().getAllErrors().get(0);
        FieldError fieldError = (FieldError) ex.getBindingResult().getAllErrors().get(0);
        // 获取字段注解属性kv
        Map<String, Object> attributes = getErrorFiledAnnotationAttributesMap(() -> ReflectionUtils.getFieldAnnotationsAttributes(Objects.requireNonNull(ex.getBindingResult().getTarget()).getClass(), fieldError.getField()));

        return buildApiResponseResponseEntity(ex, ErrorCodeEnums.ERROR_REQUEST_PARAMS_INVALID.getCode(), handlerErrorMessage(error.getDefaultMessage(), fieldError.getField(), attributes), HttpStatus.BAD_REQUEST);
    }

    /**
     * 请求参数值校验错误异常捕获(List类型参数校验)
     * @param ex 请求接口参数值校验异常
     * @return {@link com.solo.framework.web.response.ApiResponseAbstract<Void>}
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponseAbstract<Void>> handleValidationExceptions(ConstraintViolationException ex) {
        ConstraintViolation<?> error = ex.getConstraintViolations().stream().iterator().next();
        // 获取字段注解属性kv
        Map<String, Object> attributes = getErrorFiledAnnotationAttributesMap(() -> error.getConstraintDescriptor().getAttributes());

        return buildApiResponseResponseEntity(ex, ErrorCodeEnums.ERROR_REQUEST_PARAMS_INVALID.getCode(), handlerErrorMessage(error.getMessage(), error.getPropertyPath().toString(), attributes), HttpStatus.BAD_REQUEST);
    }

    /**
     * 请求接口参数格式错误异常捕获(如@RequestParam参数未传)
     * @param ex 请求接口参数错误异常
     * @return {@link com.solo.framework.web.response.ApiResponseAbstract<Void>}
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponseAbstract<Void>> handleNoHandlerFoundException(MissingServletRequestParameterException ex) {
        return buildApiResponseResponseEntity(ex, ErrorCodeEnums.ERROR_REQUEST_PARAMS_FORMAT_INVALID, HttpStatus.BAD_REQUEST);
    }

    /**
     * 请求接口参数格式错误异常捕获(兜底, 如JSON Object格式传参为List格式)
     * @param ex 请求接口参数错误异常
     * @return {@link com.solo.framework.web.response.ApiResponseAbstract<Void>}
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponseAbstract<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return buildApiResponseResponseEntity(ex, ErrorCodeEnums.ERROR_REQUEST_PARAMS_FORMAT_INVALID, HttpStatus.BAD_REQUEST);
    }

    /**
     * 框架自定义错误异常捕获
     * @param ex 框架自定义错误异常
     * @return {@link com.solo.framework.web.response.ApiResponseAbstract<Void>}
     */
    @ExceptionHandler(IErrorException.class)
    public ResponseEntity<ApiResponseAbstract<Void>> handleIErrorException(IErrorException ex) {
        return buildApiResponseResponseEntity(ex);
    }

    /**
     * 系统级别错误异常捕获
     * @param ex 未捕获的其它所有错误异常,兜底处理
     * @return {@link com.solo.framework.web.response.ApiResponseAbstract<Void>}
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiResponseAbstract<Void>> handleIErrorException(Throwable ex) {
        return buildApiResponseResponseEntity(ex, ErrorCodeEnums.ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 判断类或方法上是否标注了${@link com.solo.framework.web.annotation.NoApiResponse}注解
     * @param methodParameter 方法参数
     * @return 是否标注了注解
     */
    protected boolean hasNoApiResponseAnnotation(MethodParameter methodParameter) {
        if (methodParameter.hasMethodAnnotation(NoApiResponse.class)) {
            return true;
        }

        Class<?> beanType = methodParameter.getContainingClass();
        return beanType.isAnnotationPresent(NoApiResponse.class);
    }

    /**
     * 判断是否是Swagger请求
     * @param methodParameter 方法参数
     * @return 是否是Swagger请求
     */
    protected boolean hasSwaggerRequest(@NonNull MethodParameter methodParameter) {
        return methodParameter.getContainingClass().getName().contains("springfox")
                || methodParameter.getContainingClass().getName().contains("knife4j");
    }

    /**
     * 检查请求响应是否是404
     * @param body 请求响应体
     */
    protected void checkRequestHttp404(Object body) {
        if (body instanceof Map) {
            Map<?, ?> responseMap = (Map<?, ?>) body;
            Object statusObj = responseMap.get(REQUEST_NOT_FOUND_STATUS);
            Object errorObj = responseMap.get(REQUEST_NOT_FOUND_ERROR);
            Object pathObj = responseMap.get(REQUEST_NOT_FOUND_PATH);
            if (Integer.valueOf(HttpStatus.NOT_FOUND.value()).equals(statusObj) && REQUEST_NOT_FOUND_MESSAGE.equals(errorObj)) {
                throw new IErrorHttpNoFoundException(ErrorCodeEnums.ERROR_REQUEST_URI_INVALID.getCode(), Objects.toString(errorObj, "") + ": " + Objects.toString(pathObj, ""));
            }
        }
    }

    /**
     * 构建请求响应
     * @param ex 捕获到的异常类型
     * @return 请求响应体
     */
    protected ResponseEntity<ApiResponseAbstract<Void>> buildApiResponseResponseEntity(IErrorException ex) {
        return buildApiResponseResponseEntity(ex, ex.getErrorCode(), ex.getErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 构建请求响应
     * @param ex 捕获到的异常类型
     * @param iErrorCodeEnum 错误枚举
     * @param httpStatus 请求http响应码
     * @return 请求响应体
     */
    protected ResponseEntity<ApiResponseAbstract<Void>> buildApiResponseResponseEntity(Throwable ex, ErrorCodeEnums iErrorCodeEnum, HttpStatus httpStatus) {
        return buildApiResponseResponseEntity(ex, iErrorCodeEnum.getCode(), iErrorCodeEnum.getMessage(), httpStatus);
    }

    /**
     * 构建请求响应
     * @param ex 捕获到的异常类型
     * @param resultCode 请求响应码
     * @param message 请求响应提示信息
     * @param httpStatus 请求http响应码
     * @return 请求响应体
     */
    protected ResponseEntity<ApiResponseAbstract<Void>> buildApiResponseResponseEntity(Throwable ex, Integer resultCode, String message, HttpStatus httpStatus) {
        ApiResponseAbstract<Void> apiErrorResponse = ApiResponse.error(resultCode, message, ex);
        printExceptionLog(ex);
        return new ResponseEntity<>(apiErrorResponse, httpStatus);
    }

    /**
     * 打印异常日志
     * @param ex 捕获到的异常类型
     */
    protected void printExceptionLog(Throwable ex) {
        LogUtil.log("服务异常,请查看错误日志", ObjectUtil.defaultIfNull(getExceptionLogLevel(ex), SoloFrameworkLoggingEnum.ERROR), ex);
    }

    /**
     * 获取校验错误字段注解属性Map
     * @param func 自定义无参消费型接口
     * @return 校验错误字段注解属性Map(k-属性名称,v-属性值)
     */
    private static Map<String, Object> getErrorFiledAnnotationAttributesMap(NoArgSupplier<Map<String, Object>> func) {
        return SoloFrameworkRuntimeInfo.INSTANCE.getSoloFrameworkProperties().getWeb().getInternation().isEnabled() ? func.get() : MapUtil.newHashMap();
    }

    /**
     * 处理校验错误信息
     * @param errorMessage 校验错误信息
     * @param fieldName 校验错误字段
     * @param attributes 校验错误字段注解属性Map(k-属性名称,v-属性值)
     * @return 处理后的校验错误信息
     */
    private static String handlerErrorMessage(String errorMessage, String fieldName, Map<String, Object> attributes) {
        // 是否开启了国际化
        if (SoloFrameworkRuntimeInfo.INSTANCE.getSoloFrameworkProperties().getWeb().getInternation().isEnabled()) {
            // 消息内容国际化处理, 并且替换占位符
            String message = MessageUtil.getInternationPlaceholdersMessage(errorMessage, attributes);
            return ErrorCodeEnums.ERROR_REQUEST_PARAMS_INVALID.getMessage() + ":[" + fieldName + "-" + message + "]";
        } else {
            return errorMessage;
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
