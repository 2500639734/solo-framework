package com.solo.framework.web.response;

public interface IResponse<T> {

    /**
     * 获取请求响应码
     * @return 请求响应码
     */
    int getResultCode();

    /**
     * 获取请求响应提示信息
     * @return 请求响应提示信息
     */
    String getMessage();

    /**
     * 获取请求返参数据
     * @return 获取请求返参数据
     */
    T getData();

    /**
     * 获取请求Id
     * @return 请求Id
     */
    String getTraceId();

    /**
     * 获取请求异常的类路径
     * @return 请求异常的类路径
     */
    String getExceptionClass();

    /**
     * 获取请求异常原始信息(适用于非自定义异常的场景)
     * @return 请求异常原始信息(适用于非自定义异常的场景)
     */
    String getExceptionMessage();

    /**
     * 获取请求响应时间戳
     * @return 请求响应时间戳
     */
    long getTimestamp();

    /**
     * 获取接口异常信息
     * @return 接口异常信息
     */
    Throwable getException();

}
