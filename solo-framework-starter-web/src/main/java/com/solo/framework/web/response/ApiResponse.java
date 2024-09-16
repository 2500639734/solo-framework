package com.solo.framework.web.response;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solo.framework.web.enums.IErrorCode;
import com.solo.framework.web.enums.IErrorCodeEnums;
import com.solo.framework.web.exception.IErrorException;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

@ApiModel(description = "Api响应结果")
@Data
@Accessors(chain = true)
public class ApiResponse<T> implements IResponse<T>, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 请求响应码
     */
    private int resultCode;

    /**
     * 请求响应提示信息
     */
    private String message;

    /**
     * 请求返参数据
     */
    private T data;

    /**
     * 请求Id
     */
    private String traceId;

    /**
     * 请求异常的类路径
     */
    private String exceptionClass;

    /**
     * 请求异常原始信息(适用于非自定义异常的场景)
     */
    private String exceptionMessage;

    /**
     * 请求响应时间戳
     */
    private long timestamp;

    /**
     * 接口异常信息
     */
    @JSONField(serialize = false, deserialize = false)
    @JsonIgnore
    private transient Throwable exception;

    public static <T> ApiResponse<T> success(T data) {
        return success(IErrorCodeEnums.SUCCESS, data);
    }

    public static <T> ApiResponse<T> success(IErrorCode iErrorCode, T data) {
        return success(iErrorCode.getResultCode(), iErrorCode.getMessage(), data);
    }

    public static <T> ApiResponse<T> success(int resultCode, String message, T data) {
        return buildApiResponse(resultCode, message, null, data, null);
    }

    public static <T> ApiResponse<T> error(int resultCode, String message, Throwable exception) {
        return error(resultCode, message, null, exception);
    }

    public static <T> ApiResponse<T> error(IErrorCode iErrorCode, Throwable exception) {
        return error(iErrorCode, null, exception);
    }

    public static <T> ApiResponse<T> error(IErrorCode iErrorCode, String traceId, Throwable exception) {
        return error(iErrorCode.getResultCode(), iErrorCode.getMessage(), traceId, exception);
    }

    public static <T> ApiResponse<T> error(int resultCode, String message, String traceId, Throwable exception) {
        return buildApiResponse(resultCode, message, traceId, exception);
    }

    public static <T> ApiResponse<T> buildApiResponse(int resultCode, String message, String traceId, Throwable exception) {
        return buildApiResponse(resultCode, message, traceId, null, exception);
    }

    private static <T> ApiResponse<T> buildApiResponse(int resultCode, String message, String traceId, T data, Throwable exception) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setResultCode(resultCode);
        response.setMessage(message);
        response.setData(data);
        response.setTraceId(traceId);
        response.setTimestamp(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        if (Objects.nonNull(exception)) {
            response.setException(exception);
            response.setExceptionClass(exception.getClass().getName());
            if (exception instanceof IErrorException) {
                response.setExceptionMessage(((IErrorException) exception).getErrorMessage());
            } else {
                response.setExceptionMessage(exception.getMessage());
            }
        }
        return response;
    }

}
