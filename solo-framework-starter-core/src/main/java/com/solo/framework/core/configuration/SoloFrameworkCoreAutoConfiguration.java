package com.solo.framework.core.configuration;

import cn.hutool.core.collection.CollUtil;
import com.solo.framework.core.context.SoloFrameworkContextHolder;
import com.solo.framework.core.env.SoloFrameworkRuntimeInfo;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class SoloFrameworkCoreAutoConfiguration implements Ordered {

    @Bean
    public SoloFrameworkRuntimeInfo soloFrameworkRuntimeInfo() {
        return new SoloFrameworkRuntimeInfo().setBasePackages(getApplicationScanBasePackages(SoloFrameworkContextHolder.getApplicationContext()));
    }

    /**
     * 获取当前应用的扫描包路径
     * @param applicationContext 容器上下文
     * @return 当前应用的扫描包路径
     */
    private Set<String> getApplicationScanBasePackages(ApplicationContext applicationContext) {
        // 默认
        if (Objects.isNull(applicationContext)) {
            return CollUtil.newHashSet("/");
        }

        // 优先取@ComponentScan
        Set<String> scanBasePackages = getComponentScanBasePackages(applicationContext);
        if (CollUtil.isNotEmpty(scanBasePackages)) {
            return scanBasePackages;
        }

        // 如果未标注@ComponentScan，取标注@SpringBootApplication的包路径
        scanBasePackages = getApplicationDefaultScanBasePackages(applicationContext);
        return scanBasePackages;
    }

    /**
     * 获取当前应用的扫描包路径(基于@ComponentScan)
     * @param applicationContext 容器上下文
     * @return 当前应用的扫描包路径
     */
    private Set<String> getComponentScanBasePackages(ApplicationContext applicationContext) {
        return applicationContext
                .getBeansWithAnnotation(ComponentScan.class).values().stream()
                .map(bean -> AnnotatedElementUtils.findMergedAnnotationAttributes(bean.getClass(), ComponentScan.class, false, true))
                .filter(Objects::nonNull)
                .flatMap(attributes -> Stream.of(attributes.getStringArray("basePackages")))
                .filter(basePackage -> ! basePackage.contains("springfox") && ! basePackage.contains("com.github"))
                .collect(Collectors.toSet());
    }

    /**
     * 获取当前应用的扫描包路径(基于@SpringBootApplication(未指定scanBasePackages时))
     * @param applicationContext 容器上下文
     * @return 当前应用的扫描包路径
     */
    private Set<String> getApplicationDefaultScanBasePackages(ApplicationContext applicationContext) {
        return Arrays.stream(applicationContext.getBeanDefinitionNames())
                .map(beanDefinitionName -> {
                    try {
                        Object bean = applicationContext.getBean(beanDefinitionName);
                        Class<?> clazz = bean.getClass();
                        if (clazz.getAnnotation(SpringBootApplication.class) != null) {
                            return clazz.getPackage().getName();
                        }
                    } catch (Exception e) {
                        // skip 这里仅为了获取默认的scanBasePackages, 可能会报错循环依赖, 不会影响框架启动, 忽略这个异常
                    }
                    return null;
                }).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @Override
    public int getOrder() {
        // org.springframework.beans.factory.config.BeanFactoryPostProcessor
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
