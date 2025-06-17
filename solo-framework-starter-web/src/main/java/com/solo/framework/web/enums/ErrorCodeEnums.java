package com.solo.framework.web.enums;

import com.solo.framework.core.env.SoloFrameworkRuntimeInfo;
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
public enum ErrorCodeEnums implements IErrorCode {

    /**
     * 系统级别异常定义
     */
    SUCCESS                                                 (0, "success.message", "请求成功"),
    ERROR                                                   (-1, "error.message", "服务器错误, 请联系运维人员处理"),
    ERROR_REQUEST_REPEAT                                    (-2, "error.request.repeat.message", "服务器繁忙, 请稍后重试"),
    ERROR_REQUEST_URI_INVALID                               (-3, "error.uri.invalid.message", "请求地址无效"),
    ERROR_REQUEST_WAY_INVALID                               (-4, "error.way.invalid.message", "请求方式错误"),
    ERROR_REQUEST_PARAMS_INVALID                            (-5, "request.error.params.invalid.message", "请求参数缺失或无效"),
    ERROR_REQUEST_PARAMS_FORMAT_INVALID                     (-6, "request.error.params.format.invalid.message", "请求参数格式不符合要求"),
    ERROR_REQUEST_REQUEST_FAIL                              (-7, "request.error.request.fail.message", "请求远程调用失败"),
    ERROR_REQUEST_NETWORK_CONNECTION_FAIL                   (-8, "request.error.network.connection.fail.message", "请求网络连接失败"),;

    /**
     * 错误码：0-成功，其它失败
     */
    private final Integer code;

    /**
     * 提示信息（开启国际化）
     */
    private final String message;

    /**
     * 提示信息（未开启国际化）
     */
    private final String defaultMessage;

    /**
     * 根据请求响应码 获取请求响应枚举
     * @param code 请求响应码
     * @return 请求响应枚举
     */
    public static ErrorCodeEnums getByCode(int code){
        return Arrays.stream(ErrorCodeEnums.values())
                .filter(ErrorCodeEnums -> ErrorCodeEnums.code.equals(code))
                .findFirst().orElse(null);
    }

    public String getMessage() {
        return SoloFrameworkRuntimeInfo.INSTANCE.getSoloFrameworkProperties().getWeb().getInternation().isEnabled() ? message : defaultMessage;
    }

}
