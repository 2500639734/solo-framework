package com.solo.framework.core.properties.web;

import com.solo.framework.core.constant.SoloFrameworkPropertiesPrefixConstant;
import com.solo.framework.core.properties.web.fastjson.SoloFrameworkWebFastJsonProperties;
import com.solo.framework.core.properties.web.response.SoloFrameworkWebResponseProperties;
import com.solo.framework.core.properties.web.swagger.SoloFrameworkWebSwaggerProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
@ConfigurationProperties(prefix = SoloFrameworkPropertiesPrefixConstant.SOLO_FRAMEWORK_WEB_PREFIX)
public class SoloFrameworkWebProperties {

    /**
     * 框架swagger相关配置
     */
    @NestedConfigurationProperty
    private SoloFrameworkWebSwaggerProperties swagger = new SoloFrameworkWebSwaggerProperties();

    /**
     * 框架fast json相关配置
     */
    @NestedConfigurationProperty
    private SoloFrameworkWebFastJsonProperties fastjson = new SoloFrameworkWebFastJsonProperties();

    /**
     * 框架response相关配置
     */
    @NestedConfigurationProperty
    private SoloFrameworkWebResponseProperties response = new SoloFrameworkWebResponseProperties();

}
