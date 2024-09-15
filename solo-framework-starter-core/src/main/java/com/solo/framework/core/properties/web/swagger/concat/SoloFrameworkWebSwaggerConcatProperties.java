package com.solo.framework.core.properties.web.swagger.concat;

import com.solo.framework.core.constant.SoloFrameworkPropertiesPrefixConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = SoloFrameworkPropertiesPrefixConstant.SOLO_FRAMEWORK_WEB_SWAGGER_CONCAT_PREFIX)
public class SoloFrameworkWebSwaggerConcatProperties {

    private String name;
    private String url;
    private String email;

}
