package com.solo.framework.web.configuration.web.response;

import com.solo.framework.core.properties.web.response.SoloFrameworkWebResponseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SoloFrameworkWebResponseProperties.class)
public class SoloFrameworkWebResponseAutoConfiguration {
}
