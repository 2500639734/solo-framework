package com.solo.framework.web.response;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solo.framework.web.enums.IErrorCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NonNull;

@ApiModel(value = "ApiResponse", description = "基础响应类，包含请求响应码、请求响应提示信息、请求返参数据、请求Id、请求响应时间戳等信息")
@Data
public abstract class ApiResponseAbstract<T> {

    /**
     * 请求响应码
     */
    @ApiModelProperty(value = "请求响应码", example = "200")
    private int code;

    /**
     * 请求响应提示信息
     */
    @ApiModelProperty(value = "请求响应提示信息", example = "请求成功")
    private String message;

    /**
     * 请求返参数据
     */
    @ApiModelProperty(value = "请求返参数据", example = "{}")
    private T data;

    /**
     * 请求Id
     */
    @ApiModelProperty(value = "请求Id", example = "e6eb03c7-7aaa-4982-96e7-b831c3f0eb0b")
    private String traceId;

    /**
     * 请求异常的类路径
     */
    @ApiModelProperty(value = "请求异常的类路径", example = "com.solo.framework.web.response.ApiResponseAbstract")
    private String exceptionClass;

    /**
     * 请求异常信息(适用于非自定义异常的场景)
     * - 禁止序列化,通过日志查看详细错误信息
     */
    @JSONField(serialize = false, deserialize = false)
    @JsonIgnore
    private String exceptionMessage;

    /**
     * 请求响应时间戳
     */
    @ApiModelProperty(value = "请求响应时间戳", example = "1764429597657")
    private long timestamp;

    /**
     * 请求异常详细信息(包含堆栈信息),禁止序列化
     */
    @JSONField(serialize = false, deserialize = false)
    @JsonIgnore
    private transient Throwable exception;

    protected abstract ApiResponseAbstract<T> successResponse(T data);

    protected abstract ApiResponseAbstract<T> successResponse(@NonNull IErrorCode iErrorCode, T data);

    protected abstract ApiResponseAbstract<T> successResponse(@NonNull int code, @NonNull String message, T data);

    protected abstract ApiResponseAbstract<T> errorResponse(@NonNull IErrorCode iErrorCode, Throwable exception);

    protected abstract ApiResponseAbstract<T> errorResponse(@NonNull int code, @NonNull String message, Throwable exception);

    protected abstract ApiResponseAbstract<T> errorResponse(@NonNull int code, @NonNull String message, T data, Throwable exception);

    protected abstract ApiResponseAbstract<T> buildResponse(@NonNull int code, @NonNull String message, T data, Throwable exception);

}
