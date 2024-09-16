package com.solo.framework.web.enums;

/**
 * 业务异常枚举统一接口
 */
public interface IErrorCode {

    /**
     * 获取请求响应码
     * @return 请求响应码
     */
    Integer getResultCode();

    /**
     * 获取请求响应信息
     * @return 请求响应信息
     */
    String getMessage();

}
