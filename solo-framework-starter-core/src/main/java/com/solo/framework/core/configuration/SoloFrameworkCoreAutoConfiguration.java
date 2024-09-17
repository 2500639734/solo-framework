package com.solo.framework.core.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class SoloFrameworkCoreAutoConfiguration implements Ordered {

    @Override
    public int getOrder() {
        // org.springframework.beans.factory.config.BeanFactoryPostProcessor
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
