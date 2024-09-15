package com.solo.framework.core.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

public class SoloFrameworkContextHolder {

    private static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (getApplicationContext() == null) {
            SoloFrameworkContextHolder.applicationContext = applicationContext;
            return;
        }
        if (getApplicationContext().equals(applicationContext.getParent())) {
            SoloFrameworkContextHolder.applicationContext = applicationContext;
        }
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static Object getBean(String beanName) {
        return applicationContext.getBean(beanName);
    }

    public static <T> T getBean(String beanName, Class<T> clazz) {
        return applicationContext.getBean(beanName, clazz);
    }

    public static <T> T getBean(Class<T> beanType) {
        return applicationContext.getBean(beanType);
    }

}
