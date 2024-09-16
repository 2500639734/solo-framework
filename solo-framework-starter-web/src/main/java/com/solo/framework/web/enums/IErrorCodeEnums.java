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

    SUCCESS(0, "请求成功"),
    ERROR(-1, "服务器繁忙, 请稍后重试"),
    ERROR_REQUEST_URI_INVALID(-2, "请求地址无效"),
    ERROR_REQUEST_PARAMS_INVALID(-3, "请求参数缺失或无效"),
    ERROR_REQUEST_PARAMS_FORMAT_INVALID(-4, "请求参数格式不符合要求"),
    ERROR_REQUEST_REQUEST_FAIL(-5, "请求远程调用失败"),
    ERROR_REQUEST_NETWORK_CONNECTION_FAIL(-6, "请求网络连接失败"),;

    /**
     * 错误码：0-成功，其它失败
     */
    private final Integer resultCode;

    /**
     * 提示信息
     */
    private final String message;

    /**
     * 根据请求响应码 获取请求响应枚举
     * @param resultCode 请求响应码
     * @return 请求响应枚举
     */
    public static IErrorCodeEnums getByResultCode(int resultCode){
        return Arrays.stream(IErrorCodeEnums.values())
                .filter(IErrorCodeEnums -> IErrorCodeEnums.resultCode.equals(resultCode))
                .findFirst().orElse(null);
    }

    @Override
    public Integer getResultCode() {
        return resultCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
