package com.solo.framework.web.configuration.web.swagger;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.solo.framework.core.constant.SoloFrameworkPropertiesPrefixConstant;
import com.solo.framework.core.env.SoloFrameworkRuntimeInfo;
import com.solo.framework.core.properties.web.swagger.SoloFrameworkWebSwaggerProperties;
import com.solo.framework.core.properties.web.swagger.concat.SoloFrameworkWebSwaggerConcatProperties;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Set;
import java.util.function.Predicate;


@Slf4j
@Configuration
@ConditionalOnProperty(name = SoloFrameworkPropertiesPrefixConstant.SOLO_FRAMEWORK_WEB_SWAGGER_PREFIX + ".enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(SoloFrameworkWebSwaggerProperties.class)
public class SoloFrameworkWebSwaggerAutoConfiguration {

    @Setter(onMethod_ = {@Autowired})
    private SoloFrameworkWebSwaggerProperties soloFrameworkWebSwaggerProperties;

    @Bean
    @ConditionalOnMissingBean(Docket.class)
    public Docket soloFrameworkSwaggerDocket(SoloFrameworkRuntimeInfo soloFrameworkRuntimeInfo) {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo(soloFrameworkWebSwaggerProperties))
                .enable(true)
                .select()
                .apis(requestHandlerPredicate(soloFrameworkRuntimeInfo))
                .paths(PathSelectors.any())
                .build();
    }

    private Predicate<RequestHandler> requestHandlerPredicate(SoloFrameworkRuntimeInfo soloFrameworkRuntimeInfo) {
        Set<String> scanBasePackages = CollUtil.defaultIfEmpty(soloFrameworkWebSwaggerProperties.getBasePackages(), soloFrameworkRuntimeInfo.getBasePackages());
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

}
