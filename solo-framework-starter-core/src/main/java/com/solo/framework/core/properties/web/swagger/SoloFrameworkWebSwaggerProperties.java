package com.solo.framework.core.properties.web.swagger;

import com.solo.framework.core.constant.SoloFrameworkPropertiesPrefixConstant;
import com.solo.framework.core.properties.web.swagger.concat.SoloFrameworkWebSwaggerConcatProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.Set;


@Data
@ConfigurationProperties(prefix = SoloFrameworkPropertiesPrefixConstant.SOLO_FRAMEWORK_WEB_SWAGGER_PREFIX)
public class SoloFrameworkWebSwaggerProperties {

    /**
     * 是否启用swagger
     */
    private boolean enabled = false;

    /**
     * swagger扫描的包名
     */
    private Set<String> basePackages;

    /**
     * swagger文档标题
     */
    private String title = "Solo Framework API Documentation";

    /**
     * swagger文档描述
     */
    private String description = "API documentation for your application";

    /**
     * 条款跳转地址
     */
    private String termsOfServiceUrl;

    /**
     * 文档版本
     */
    private String version = "v1.0.0";

    /**
     * swagger文档联系人配置
     */
    @NestedConfigurationProperty
    private SoloFrameworkWebSwaggerConcatProperties concat = new SoloFrameworkWebSwaggerConcatProperties();

}
