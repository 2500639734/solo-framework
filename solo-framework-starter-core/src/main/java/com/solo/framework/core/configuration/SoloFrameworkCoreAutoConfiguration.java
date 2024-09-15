package com.solo.framework.core.configuration;

import com.solo.framework.core.context.SoloFrameworkContextInitializerBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class SoloFrameworkCoreAutoConfiguration implements Ordered {

    @Bean
    @ConditionalOnMissingBean
    public SoloFrameworkContextInitializerBean soloFrameworkContextInitializerBean() {
        return new SoloFrameworkContextInitializerBean();
    }

    @Override
    public int getOrder() {
        // org.springframework.beans.factory.config.BeanFactoryPostProcessor
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
