package com.solo.framework.web.configuration.web.response;

import com.solo.framework.core.properties.web.response.SoloFrameworkWebResponseProperties;
import com.solo.framework.web.handle.ApiResponseAdvice;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SoloFrameworkWebResponseProperties.class)
public class SoloFrameworkWebResponseAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(ApiResponseAdvice.class)
    public ApiResponseAdvice apiResponseAdvice() {
        return new ApiResponseAdvice();
    }

}
