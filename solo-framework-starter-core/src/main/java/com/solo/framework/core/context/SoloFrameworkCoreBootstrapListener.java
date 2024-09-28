package com.solo.framework.core.context;

import com.solo.framework.core.env.SoloFrameworkRuntimeInfo;
import com.solo.framework.core.properties.SoloFrameworkProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;

@Slf4j
public class SoloFrameworkCoreBootstrapListener implements ApplicationListener<ApplicationEvent>, Ordered {

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationReadyEvent) {
            ApplicationContext applicationContext = SoloFrameworkContextHolder.getApplicationContext();
            Environment environment = applicationContext.getEnvironment();
            SoloFrameworkRuntimeInfo.INSTANCE
                    .setApplicationName(environment.getProperty(SoloFrameworkRuntimeInfo.APPLICATION_NAME))
                    // .setBasePackages(SoloFrameworkApplicationUtil.getApplicationScanBasePackages(applicationContext))
                    .setSoloFrameworkProperties(SoloFrameworkContextHolder.getBean(SoloFrameworkProperties.class))
                    .setVersion(environment.getProperty(SoloFrameworkRuntimeInfo.VERSION));
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
