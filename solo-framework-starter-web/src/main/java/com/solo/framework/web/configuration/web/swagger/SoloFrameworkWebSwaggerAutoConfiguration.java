package com.solo.framework.web.configuration.web.swagger;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.solo.framework.core.constant.SoloFrameworkPropertiesPrefixConstant;
import com.solo.framework.core.context.SoloFrameworkContextHolder;
import com.solo.framework.core.env.SoloFrameworkRuntimeInfo;
import com.solo.framework.core.properties.web.swagger.SoloFrameworkWebSwaggerProperties;
import com.solo.framework.core.properties.web.swagger.concat.SoloFrameworkWebSwaggerConcatProperties;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
@Configuration
@ConditionalOnProperty(name = SoloFrameworkPropertiesPrefixConstant.SOLO_FRAMEWORK_WEB_SWAGGER_PREFIX + ".enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(SoloFrameworkWebSwaggerProperties.class)
public class SoloFrameworkWebSwaggerAutoConfiguration {

    @Setter(onMethod_ = {@Autowired})
    private SoloFrameworkWebSwaggerProperties soloFrameworkWebSwaggerProperties;

    @Bean
    @ConditionalOnMissingBean(Docket.class)
    public Docket soloFrameworkSwaggerDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo(soloFrameworkWebSwaggerProperties))
                .enable(true)
                .select()
                .apis(requestHandlerPredicate())
                .paths(PathSelectors.any())
                .build();
    }

    private Predicate<RequestHandler> requestHandlerPredicate() {
        Set<String> scanBasePackages = CollUtil.defaultIfEmpty(soloFrameworkWebSwaggerProperties.getBasePackages(), getApplicationScanBasePackages(SoloFrameworkContextHolder.getApplicationContext()));
        log.info("==============>>>scanBasePackages: {}", scanBasePackages);
        return scanBasePackages
                .stream()
                .map(RequestHandlerSelectors::basePackage)
                .reduce(Predicate::or)
                .orElse(input -> false);
    }

    private ApiInfo apiInfo(SoloFrameworkWebSwaggerProperties soloFrameworkWebSwaggerProperties) {
        SoloFrameworkWebSwaggerConcatProperties concat = soloFrameworkWebSwaggerProperties.getConcat();
        return new ApiInfoBuilder()
                .title(soloFrameworkWebSwaggerProperties.getTitle())
                .description(soloFrameworkWebSwaggerProperties.getDescription())
                .termsOfServiceUrl(soloFrameworkWebSwaggerProperties.getTermsOfServiceUrl())
                .contact(new Contact(concat.getName(), concat.getUrl(), concat.getEmail()))
                .version(StrUtil.blankToDefault(soloFrameworkWebSwaggerProperties.getVersion(), SoloFrameworkRuntimeInfo.VERSION))
                .build();
    }

    private Set<String> getApplicationScanBasePackages(ApplicationContext applicationContext) {
        if (Objects.isNull(applicationContext)) {
            return CollUtil.newHashSet("/");
        }

        Set<String> scanBasePackages = getComponentScanBasePackages(applicationContext);
        if (CollUtil.isNotEmpty(scanBasePackages)) {
            return scanBasePackages;
        }

        scanBasePackages = getApplicationDefaultScanBasePackages(applicationContext);
        return scanBasePackages;
    }

    private Set<String> getComponentScanBasePackages(ApplicationContext applicationContext) {
        return applicationContext
                .getBeansWithAnnotation(ComponentScan.class).values().stream()
                .map(bean -> AnnotatedElementUtils.findMergedAnnotationAttributes(bean.getClass(), ComponentScan.class, false, true))
                .filter(Objects::nonNull)
                .flatMap(attributes -> Stream.of(attributes.getStringArray("basePackages")))
                .filter(basePackage -> ! basePackage.contains("springfox") && ! basePackage.contains("com.github"))
                .collect(Collectors.toSet());
    }

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

}
