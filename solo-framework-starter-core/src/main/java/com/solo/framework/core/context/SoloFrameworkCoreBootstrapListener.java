package com.solo.framework.core.context;

import com.solo.framework.core.env.SoloFrameworkRuntimeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;

public class SoloFrameworkCoreBootstrapListener implements ApplicationListener<ApplicationEvent>, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(SoloFrameworkCoreBootstrapListener.class);

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationReadyEvent) {
            Environment environment = SoloFrameworkContextHolder.getApplicationContext().getEnvironment();
            SoloFrameworkRuntimeInfo.INSTANCE
                    .setApplicationName(environment.getProperty(SoloFrameworkRuntimeInfo.APPLICATION_NAME))
                    .setVersion(environment.getProperty(SoloFrameworkRuntimeInfo.VERSION));

            // if (logger.isDebugEnabled()) {
                logger.info("=======================>>> SoloFrameworkRuntimeInfo = {}", SoloFrameworkRuntimeInfo.INSTANCE);
            // }
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
