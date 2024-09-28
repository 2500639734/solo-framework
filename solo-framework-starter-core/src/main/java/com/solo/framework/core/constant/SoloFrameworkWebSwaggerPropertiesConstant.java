package com.solo.framework.core.constant;

public class SoloFrameworkWebSwaggerPropertiesConstant {

    private SoloFrameworkWebSwaggerPropertiesConstant() {

    }

    /**
     * 框架 swagger MapPropertySourceKey
     */
    public static final String MAP_PROPERTY_SOURCE_KEY = "soloFrameworkWebSwaggerProperties";

    /**
     * 框架 swagger 是否启用配置项 key
     */
    public static final String ENABLED_KEY = SoloFrameworkPropertiesPrefixConstant.SOLO_FRAMEWORK_WEB_SWAGGER_PREFIX + ".enabled";

    /**
     * 框架 swagger 是否启用配置项 默认值
     */
    public static final boolean ENABLED_DEFAULT_VALUE = true;

    /**
     * 真正的 swagger 是否启用配置项 key
     */
    public static final String SPRINGFOX_DOCUMENTATION_ENABLED = "springfox.documentation.enabled";

    /**
     * 真正的 ken4j 是否启用配置项 key
     */
    public static final String KEN4J_ENABLED = "knife4j.enable";

}
