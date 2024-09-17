package com.solo.framework.core.constant;

import com.solo.framework.core.context.SoloFrameworkContextHolder;
import com.solo.framework.core.env.SoloFrameworkRuntimeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;

public class SoloFrameworkWebBootstrapListener implements ApplicationListener<ApplicationEvent> {

    private static final Logger logger = LoggerFactory.getLogger(SoloFrameworkWebBootstrapListener.class);

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        Environment environment = SoloFrameworkContextHolder.getApplicationContext().getEnvironment();
        SoloFrameworkRuntimeInfo.INSTANCE
                .setApplicationName(environment.getProperty(SoloFrameworkRuntimeInfo.APPLICATION_NAME))
                .setVersion(environment.getProperty(SoloFrameworkRuntimeInfo.VERSION));

        // if (logger.isDebugEnabled()) {
            logger.info("=======================>>> SoloFrameworkRuntimeInfo = {}", SoloFrameworkRuntimeInfo.INSTANCE);
        // }
    }

}
