package com.solo.framework.core.properties.web;

import com.solo.framework.core.constant.SoloFrameworkPropertiesPrefixConstant;
import com.solo.framework.core.properties.web.swagger.SoloFrameworkWebSwaggerProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
@ConfigurationProperties(prefix = SoloFrameworkPropertiesPrefixConstant.SOLO_FRAMEWORK_WEB_PREFIX)
public class SoloFrameworkWebProperties {

    @NestedConfigurationProperty
    private SoloFrameworkWebSwaggerProperties swagger = new SoloFrameworkWebSwaggerProperties();

}
