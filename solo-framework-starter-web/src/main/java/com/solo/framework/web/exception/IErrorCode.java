package com.solo.framework.web.exception;

/**
 * 业务异常枚举统一接口
 */
public interface IErrorCode {

    /**
     * 获取错误状态码
     * @return 错误码
     */
    Integer getErrorCode();

    /**
     * 获取错误异常描述
     * @return 错误信息提示
     */
    String getErrorMsg();

}
