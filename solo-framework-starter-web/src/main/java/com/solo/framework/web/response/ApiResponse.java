package com.solo.framework.web.response;

import com.solo.framework.web.enums.IErrorCode;
import com.solo.framework.web.enums.IErrorCodeEnums;
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
    protected ApiResponseAbstract<T> successResponse(@NonNull int resultCode, @NonNull String message, T data) {
        return ApiResponse.success(resultCode, message, data);
    }

    @Override
    protected ApiResponseAbstract<T> errorResponse(@NonNull int resultCode, @NonNull String message, Throwable exception) {
        return ApiResponse.error(resultCode, message, exception);
    }

    @Override
    protected ApiResponseAbstract<T> errorResponse(@NonNull IErrorCode iErrorCode, Throwable exception) {
        return ApiResponse.error(iErrorCode, exception);
    }

    @Override
    protected ApiResponseAbstract<T> errorResponse(@NonNull IErrorCode iErrorCode, String traceId, Throwable exception) {
        return ApiResponse.error(iErrorCode, traceId, exception);
    }

    @Override
    protected ApiResponseAbstract<T> errorResponse(@NonNull int resultCode, @NonNull String message, String traceId, Throwable exception) {
        return ApiResponse.error(resultCode, message, traceId, exception);
    }

    @Override
    protected ApiResponseAbstract<T> buildResponse(@NonNull int resultCode, @NonNull String message, T data, String traceId, Throwable exception) {
        return ApiResponse.buildApiResponse(resultCode, message, data, traceId, exception);
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(IErrorCodeEnums.SUCCESS, data);
    }

    public static <T> ApiResponse<T> success(@NonNull IErrorCode iErrorCode, T data) {
        return success(iErrorCode.getResultCode(), iErrorCode.getMessage(), data);
    }

    public static <T> ApiResponse<T> success(@NonNull int resultCode, @NonNull String message, T data) {
        return buildApiResponse(resultCode, message,  data, null, null);
    }

    public static <T> ApiResponse<T> error(@NonNull int resultCode, @NonNull String message, Throwable exception) {
        return error(resultCode, message, null, exception);
    }

    public static <T> ApiResponse<T> error(@NonNull IErrorCode iErrorCode, Throwable exception) {
        return error(iErrorCode, null, exception);
    }

    public static <T> ApiResponse<T> error(@NonNull IErrorCode iErrorCode, String traceId, Throwable exception) {
        return error(iErrorCode.getResultCode(), iErrorCode.getMessage(), traceId, exception);
    }

    public static <T> ApiResponse<T> error(@NonNull int resultCode, @NonNull String message, String traceId, Throwable exception) {
        return buildApiResponse(resultCode, message, traceId, exception);
    }

    public static <T> ApiResponse<T> buildApiResponse(@NonNull int resultCode, @NonNull String message, String traceId, Throwable exception) {
        return ApiResponse.buildApiResponse(resultCode, message, null, traceId, exception);
    }

    private static <T> ApiResponse<T> buildApiResponse(@NonNull int resultCode, @NonNull String message, T data, String traceId, Throwable exception) {
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
