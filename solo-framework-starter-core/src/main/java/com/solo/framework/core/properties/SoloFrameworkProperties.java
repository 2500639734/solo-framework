package com.solo.framework.core.properties;

import com.solo.framework.core.constant.SoloFrameworkPropertiesPrefixConstant;
import com.solo.framework.core.properties.web.SoloFrameworkWebProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
@ConfigurationProperties(prefix = SoloFrameworkPropertiesPrefixConstant.SOLO_FRAMEWORK_PREFIX)
public class SoloFrameworkProperties {

    @NestedConfigurationProperty
    private SoloFrameworkWebProperties web = new SoloFrameworkWebProperties();;

}
