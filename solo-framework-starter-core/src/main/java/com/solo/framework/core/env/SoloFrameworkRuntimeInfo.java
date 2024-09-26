package com.solo.framework.core.env;

import cn.hutool.core.collection.CollUtil;
import com.solo.framework.core.context.SoloFrameworkContextHolder;
import com.solo.framework.core.properties.SoloFrameworkProperties;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Accessors(chain = true)
public class SoloFrameworkRuntimeInfo {

    public static final SoloFrameworkRuntimeInfo INSTANCE = new SoloFrameworkRuntimeInfo();

    public static final String APPLICATION_NAME = "spring.application.name";
    public static final String VERSION = "solo.framework.version";

    /**
     * 框架配置信息
     */
    private SoloFrameworkProperties soloFrameworkProperties;

    /**
     * 应用名称
     */
    private String applicationName;

    /**
     * 框架版本
     */
    private String version;

    public static Set<String> getComponentScanPackages() {
        if (Objects.isNull(SoloFrameworkContextHolder.getApplicationContext())) {
            return CollUtil.newHashSet();
        }

        return SoloFrameworkContextHolder.getApplicationContext()
                .getBeansWithAnnotation(ComponentScan.class).values().stream()
                .map(bean -> AnnotatedElementUtils.findMergedAnnotationAttributes(bean.getClass(), ComponentScan.class, false, true))
                .filter(Objects::nonNull)
                .flatMap(attributes -> Stream.of(attributes.getStringArray("basePackages")))
                .collect(Collectors.toSet());
    }

}