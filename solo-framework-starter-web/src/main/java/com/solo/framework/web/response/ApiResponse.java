package com.solo.framework.web.response;

import com.solo.framework.core.context.SoloFrameworkTraceIdContextHolder;
import com.solo.framework.web.context.SoloFrameworkWebContextHolder;
import com.solo.framework.web.enums.IErrorCode;
import com.solo.framework.web.enums.ErrorCodeEnums;
import com.solo.framework.web.exception.IErrorException;
import io.swagger.annotations.ApiModel;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

@ApiModel(description = "Api响应结果")
@Accessors(chain = true)
public class ApiResponse<T> extends ApiResponseAbstract<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    protected ApiResponseAbstract<T> successResponse(T data) {
        return ApiResponse.success(data);
    }

    @Override
    protected ApiResponseAbstract<T> successResponse(@NonNull IErrorCode iErrorCode, T data) {
        return ApiResponse.success(iErrorCode, data);
    }

    @Override
    protected ApiResponseAbstract<T> successResponse(@NonNull int code, @NonNull String message, T data) {
        return ApiResponse.success(code, message, data);
    }

    @Override
    protected ApiResponseAbstract<T> successResponse(@NonNull int code, String messageCode, @NonNull String message, T data) {
        return ApiResponse.success(code, messageCode, message, data);
    }

    @Override
    protected ApiResponseAbstract<T> errorResponse(@NonNull IErrorCode iErrorCode, Throwable exception) {
        return ApiResponse.error(iErrorCode, exception);
    }

    @Override
    protected ApiResponseAbstract<T> errorResponse(@NonNull int code, @NonNull String message, Throwable exception) {
        return ApiResponse.error(code, message, exception);
    }

    @Override
    protected ApiResponseAbstract<T> errorResponse(@NonNull int code, String messageCode, @NonNull String message, Throwable exception) {
        return ApiResponse.error(code, messageCode, message, null, exception);
    }

    @Override
    protected ApiResponseAbstract<T> buildResponse(@NonNull int code, @NonNull String message, T data, Throwable exception) {
        return ApiResponse.buildApiResponse(code, message, message, data, exception);
    }

    @Override
    protected ApiResponseAbstract<T> buildResponse(@NonNull int code, String messageCode, @NonNull String message, T data, Throwable exception) {
        return ApiResponse.buildApiResponse(code, messageCode, message, data, exception);
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(ErrorCodeEnums.SUCCESS, data);
    }

    public static <T> ApiResponse<T> success(@NonNull IErrorCode iErrorCode, T data) {
        return success(iErrorCode.getCode(), iErrorCode.getMessageCode(), iErrorCode.getMessage(), data);
    }

    public static <T> ApiResponse<T> success(@NonNull int code, @NonNull String message, T data) {
        return success(code, message, message, data);
    }

    public static <T> ApiResponse<T> success(@NonNull int code, String messageCode, @NonNull String message, T data) {
        return buildApiResponse(code, messageCode, message, data, null);
    }

    public static <T> ApiResponse<T> error(@NonNull IErrorCode iErrorCode, Throwable exception) {
        return error(iErrorCode.getCode(), iErrorCode.getMessageCode(), iErrorCode.getMessage(), null, exception);
    }

    public static <T> ApiResponse<T> error(@NonNull int code, @NonNull String message, Throwable exception) {
        return error(code, message, message, null, exception);
    }

    public static <T> ApiResponse<T> error(@NonNull int code, String messageCode, @NonNull String message, T data, Throwable exception) {
        return buildApiResponse(code, messageCode, message, data, exception);
    }

    private static <T> ApiResponse<T> buildApiResponse(@NonNull int code, String messageCode, @NonNull String message, T data, Throwable exception) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(SoloFrameworkWebContextHolder.getInternationMessage(messageCode, message));
        response.setData(data);
        response.setTraceId(SoloFrameworkTraceIdContextHolder.getTraceId());
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
