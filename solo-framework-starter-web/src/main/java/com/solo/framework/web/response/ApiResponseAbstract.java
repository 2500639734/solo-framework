package com.solo.framework.web.response;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solo.framework.web.enums.IErrorCode;
import lombok.Data;
import lombok.NonNull;

@Data
public abstract class ApiResponseAbstract<T> {

    /**
     * 请求响应码
     */
    private int code;

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
     * - 禁止序列化,通过日志查看详细错误信息
     */
    @JSONField(serialize = false, deserialize = false)
    @JsonIgnore
    private String exceptionMessage;

    /**
     * 请求响应时间戳
     */
    private long timestamp;

    /**
     * 接口异常信息,禁止序列化
     */
    @JSONField(serialize = false, deserialize = false)
    @JsonIgnore
    private transient Throwable exception;

    protected abstract ApiResponseAbstract<T> successResponse(T data);

    protected abstract ApiResponseAbstract<T> successResponse(@NonNull IErrorCode iErrorCode, T data);

    protected abstract ApiResponseAbstract<T> successResponse(@NonNull int code, @NonNull String message, T data);

    protected abstract ApiResponseAbstract<T> errorResponse(@NonNull int code, @NonNull String message, Throwable exception);

    protected abstract ApiResponseAbstract<T> errorResponse(@NonNull IErrorCode iErrorCode, Throwable exception);

    protected abstract ApiResponseAbstract<T> errorResponse(@NonNull IErrorCode iErrorCode, String traceId, Throwable exception);

    protected abstract ApiResponseAbstract<T> errorResponse(@NonNull int code, @NonNull String message, String traceId, Throwable exception);

    protected abstract ApiResponseAbstract<T> buildResponse(@NonNull int code, @NonNull String message, T data, String traceId, Throwable exception);

}
