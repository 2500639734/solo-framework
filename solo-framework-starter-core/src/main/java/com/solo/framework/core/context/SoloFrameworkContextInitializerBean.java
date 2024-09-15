package com.solo.framework.core.context;

import com.solo.framework.core.env.SoloFrameworkRuntimeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SoloFrameworkContextInitializerBean implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(SoloFrameworkContextInitializer.class);

    @Override
    public void afterPropertiesSet() {
        ApplicationContext applicationContext = SoloFrameworkContextHolder.getApplicationContext();
        Environment environment = applicationContext.getEnvironment();
        Set<String> componentScanPackages = new HashSet<>();
        try {
            Map<String, Object> componentScanBeanMap = SoloFrameworkContextHolder.getApplicationContext().getBeansWithAnnotation(ComponentScan.class);
            componentScanBeanMap.forEach((beanName, bean) -> {
                AnnotationAttributes attributes = AnnotatedElementUtils.findMergedAnnotationAttributes(bean.getClass(), ComponentScan.class, false, true);
                if (attributes == null) {
                    return;
                }

                componentScanPackages.addAll(Arrays.asList(attributes.getStringArray("basePackages")));
            });
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.info("=======================>>> 获取启动扫描包失败.不影响正常启动: {}", e.getMessage());
            }
        } finally {
            SoloFrameworkRuntimeInfo.INSTANCE
                    .setComponentScanPackages(componentScanPackages)
                    .setApplicationName(environment.getProperty(SoloFrameworkRuntimeInfo.APPLICATION_NAME))
                    .setVersion(environment.getProperty(SoloFrameworkRuntimeInfo.VERSION));

            // if (logger.isDebugEnabled()) {
                logger.info("=======================>>> SoloFrameworkRuntimeInfo = {}", SoloFrameworkRuntimeInfo.INSTANCE);
            // }
        }

    }

}
