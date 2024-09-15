package com.solo.framework.core.env;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

@Data
@Accessors(chain = true)
public class SoloFrameworkRuntimeInfo {

    public static final SoloFrameworkRuntimeInfo INSTANCE = new SoloFrameworkRuntimeInfo();

    public static final String APPLICATION_NAME = "spring.application.name";
    public static final String VERSION = "solo.framework.version";

    /**
     * 应用名称
     */
    private String applicationName;

    /**
     * 框架版本
     */
    private String version;

    /**
     * 项目配置{@link org.springframework.context.annotation.ComponentScan}的包
     */
    private Set<String> componentScanPackages;

}