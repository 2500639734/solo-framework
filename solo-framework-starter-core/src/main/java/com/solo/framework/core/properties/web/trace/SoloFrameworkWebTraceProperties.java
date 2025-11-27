package com.solo.framework.core.properties.web.trace;

import lombok.Data;

/**
 * 框架TraceId相关配置
 */
@Data
public class SoloFrameworkWebTraceProperties {

    /**
     * 是否启用TraceId上下文管理（默认开启）
     */
    private boolean enabled = true;

}
