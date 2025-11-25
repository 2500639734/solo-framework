package com.solo.framework.core.properties.web.request;

import lombok.Data;

/**
 * HTTP请求日志配置
 */
@Data
public class SoloFrameworkWebRequestLoggingProperties {

    /**
     * 是否启用HTTP请求日志
     * 默认: true
     */
    private boolean enabled = true;

    /**
     * 响应体最大打印长度
     * 默认: 2048
     */
    private int maxResponseBodyLength = 2048;

}
