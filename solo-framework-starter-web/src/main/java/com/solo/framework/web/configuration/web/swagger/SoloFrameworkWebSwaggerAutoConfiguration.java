package com.solo.framework.web.configuration.web.swagger;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.solo.framework.core.env.SoloFrameworkRuntimeInfo;
import com.solo.framework.core.properties.web.swagger.SoloFrameworkWebSwaggerProperties;
import com.solo.framework.core.properties.web.swagger.concat.SoloFrameworkWebSwaggerConcatProperties;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
import springfox.documentation.swagger2.configuration.Swagger2WebMvcConfiguration;

import java.util.Set;
import java.util.function.Predicate;


@Configuration
@EnableConfigurationProperties(SoloFrameworkWebSwaggerProperties.class)
public class SoloFrameworkWebSwaggerAutoConfiguration {

    @Setter(onMethod_ = {@Autowired})
    private SoloFrameworkWebSwaggerProperties soloFrameworkWebSwaggerProperties;

    @Bean
    @ConditionalOnClass(Swagger2WebMvcConfiguration.class)
    @ConditionalOnMissingBean(Docket.class)
    public Docket soloFrameworkSwaggerDocket() {
        if (soloFrameworkWebSwaggerProperties.isEnabled()) {
            // 优先以配置为准; 如果没有配置时默认取 @ComponentScan 扫描的包
            Set<String> componentScanPackages = CollUtil.isNotEmpty(soloFrameworkWebSwaggerProperties.getBasePackages())
                    ? soloFrameworkWebSwaggerProperties.getBasePackages() : SoloFrameworkRuntimeInfo.INSTANCE.getComponentScanPackages();
            Predicate<RequestHandler> basePackagePredicate = componentScanPackages.stream()
                    .map(RequestHandlerSelectors::basePackage)
                    .reduce(Predicate::or)
                    .orElse(input -> false);

            return new Docket(DocumentationType.SWAGGER_2)
                    .apiInfo(apiInfo(soloFrameworkWebSwaggerProperties))
                    .enable(true)
                    .select()
                    .apis(basePackagePredicate)
                    .paths(PathSelectors.any())
                    .build();
        }

        return new Docket(DocumentationType.SWAGGER_2)
                .enable(false)
                .select()
                .apis(RequestHandlerSelectors.none())
                .paths(PathSelectors.none())
                .build();
    }

    private ApiInfo apiInfo(SoloFrameworkWebSwaggerProperties swaggerConfig) {
        SoloFrameworkWebSwaggerConcatProperties concat = swaggerConfig.getConcat();
        return new ApiInfoBuilder()
                .title(swaggerConfig.getTitle())
                .description(swaggerConfig.getDescription())
                .termsOfServiceUrl(swaggerConfig.getTermsOfServiceUrl())
                .contact(new Contact(concat.getName(), concat.getUrl(), concat.getEmail()))
                .version(StrUtil.blankToDefault(swaggerConfig.getVersion(), SoloFrameworkRuntimeInfo.VERSION))
                .build();
    }

}
