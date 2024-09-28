package com.solo.framework.core.properties.web.response;

import com.solo.framework.core.constant.SoloFrameworkPropertiesPrefixConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = SoloFrameworkPropertiesPrefixConstant.SOLO_FRAMEWORK_WEB_RESPONSE_PREFIX)
public class SoloFrameworkWebResponseProperties {

    /**
     * 是否启用响应包装
     */
    private boolean enabled = true;

}
