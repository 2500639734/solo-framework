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

    private boolean enabled = true;
    private Set<String> basePackages;
    private String title = "Solo Framework API Documentation";
    private String description = "API documentation for your application";
    private String termsOfServiceUrl;
    private String version = "1.0.0";
    @NestedConfigurationProperty
    private SoloFrameworkWebSwaggerConcatProperties concat = new SoloFrameworkWebSwaggerConcatProperties();

}
