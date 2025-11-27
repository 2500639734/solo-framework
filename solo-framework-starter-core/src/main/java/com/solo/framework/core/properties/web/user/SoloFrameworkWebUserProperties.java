package com.solo.framework.core.properties.web.user;

import lombok.Data;

/**
 * 框架用户信息相关配置
 */
@Data
public class SoloFrameworkWebUserProperties {

    /**
     * 是否启用用户信息上下文管理（默认开启）
     * 开启后会注入默认系统用户实现，如果有自定义实现则使用自定义实现
     */
    private boolean enabled = true;

}
