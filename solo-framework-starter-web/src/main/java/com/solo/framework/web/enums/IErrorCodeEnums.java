package com.solo.framework.web.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Arrays;

/**
 * 系统级别通用错误码枚举定义
 *  使用自定义错误码: implements {@link IErrorCode}
 */
@RequiredArgsConstructor
@Getter
@ToString
public enum IErrorCodeEnums implements IErrorCode {

    /**
     * 系统级别异常定义
     */
    SUCCESS(0, "请求成功"),
    ERROR(-1, "服务器错误, 请联系运维人员处理"),
    ERROR_REQUEST_REPEAT(-2, "服务器繁忙, 请稍后重试"),
    ERROR_REQUEST_URI_INVALID(-3, "请求地址无效"),
    ERROR_REQUEST_PARAMS_INVALID(-4, "请求参数缺失或无效"),
    ERROR_REQUEST_PARAMS_FORMAT_INVALID(-5, "请求参数格式不符合要求"),
    ERROR_REQUEST_REQUEST_FAIL(-6, "请求远程调用失败"),
    ERROR_REQUEST_NETWORK_CONNECTION_FAIL(-7, "请求网络连接失败"),;

    /**
     * 错误码：0-成功，其它失败
     */
    private final Integer code;

    /**
     * 提示信息
     */
    private final String message;

    /**
     * 根据请求响应码 获取请求响应枚举
     * @param code 请求响应码
     * @return 请求响应枚举
     */
    public static IErrorCodeEnums getByCode(int code){
        return Arrays.stream(IErrorCodeEnums.values())
                .filter(IErrorCodeEnums -> IErrorCodeEnums.code.equals(code))
                .findFirst().orElse(null);
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
