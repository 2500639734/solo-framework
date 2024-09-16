package com.solo.framework.core.properties.web.swagger.concat;

import com.solo.framework.core.constant.SoloFrameworkPropertiesPrefixConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = SoloFrameworkPropertiesPrefixConstant.SOLO_FRAMEWORK_WEB_SWAGGER_CONCAT_PREFIX)
public class SoloFrameworkWebSwaggerConcatProperties {

    /**
     * 联系人姓名
     */
    private String name;

    /**
     * 联系人链接
     */
    private String url;

    /**
     * 联系人邮箱
     */
    private String email;

}
