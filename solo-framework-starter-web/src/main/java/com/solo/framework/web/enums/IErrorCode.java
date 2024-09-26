package com.solo.framework.web.enums;

import com.solo.framework.web.context.SoloFrameworkWebContextHolder;

/**
 * 业务异常枚举统一接口
 */
public interface IErrorCode {

    /**
     * 获取请求响应码
     * @return 请求响应码
     */
    Integer getCode();

    /**
     * 获取请求响应信息
     * @return 请求响应信息
     */
    String getMessage();

    default String getMessageCode() {
        return null;
    }

    default String getInternationMessage() {
        return SoloFrameworkWebContextHolder.getInternationMessage(getMessageCode(), getMessage());
    }

}
