package com.solo.framework.core.env;

import com.solo.framework.core.properties.SoloFrameworkProperties;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SoloFrameworkRuntimeInfo {

    public static final SoloFrameworkRuntimeInfo INSTANCE = new SoloFrameworkRuntimeInfo();

    public static final String APPLICATION_NAME = "spring.application.name";
    public static final String VERSION = "solo.framework.version";

    /**
     * 框架配置信息
     */
    private SoloFrameworkProperties soloFrameworkProperties;

    /**
     * 应用名称
     */
    private String applicationName;

    /**
     * 框架版本
     */
    private String version;

}