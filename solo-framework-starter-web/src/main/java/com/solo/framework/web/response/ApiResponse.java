package com.solo.framework.web.response;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;

@ApiModel(description = "响应结果")
@Data
@Accessors(chain = true)
public class ApiResponse<T> implements IResponse<T>, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 请求码
     */
    private int code;

    /**
     * 请求提示信息
     */
    private String message;

    /**
     * 请求返参数据
     */
    private T data;

    /**
     * 全局追踪id
     */
    private String traceId;

    /**
     * 接口异常信息
     */
    private Throwable exception;


    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage("Success");
        response.setData(data);
        return response;
    }

    public static <T> ApiResponse<T> error(int code, String message, Throwable exception, String traceId) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        response.setException(exception);
        response.setTraceId(traceId);
        return response;
    }

}
