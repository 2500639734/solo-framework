package com.solo.framework.core.context;

import com.solo.framework.core.env.SoloFrameworkRuntimeInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;

@Slf4j
public class SoloFrameworkCoreBootstrapListener implements ApplicationListener<ApplicationEvent>, Ordered {

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationReadyEvent) {
            Environment environment = SoloFrameworkContextHolder.getApplicationContext().getEnvironment();
            SoloFrameworkRuntimeInfo.INSTANCE
                    .setApplicationName(environment.getProperty(SoloFrameworkRuntimeInfo.APPLICATION_NAME))
                    .setVersion(environment.getProperty(SoloFrameworkRuntimeInfo.VERSION));

            /*if (log.isDebugEnabled()) {
                log.info("=======================>>> SoloFrameworkRuntimeInfo = {}", SoloFrameworkRuntimeInfo.INSTANCE);
            }*/
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
