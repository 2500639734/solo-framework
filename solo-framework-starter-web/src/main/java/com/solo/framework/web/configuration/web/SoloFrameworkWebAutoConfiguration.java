package com.solo.framework.web.configuration.web;

import com.alibaba.fastjson2.support.spring.http.converter.FastJsonHttpMessageConverter;
import com.solo.framework.core.properties.web.SoloFrameworkWebProperties;
import com.solo.framework.core.traceId.ISoloFrameworkTraceId;
import com.solo.framework.web.filter.SoloFrameworkTraceIdFilter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
@EnableConfigurationProperties({SoloFrameworkWebProperties.class})
public class SoloFrameworkWebAutoConfiguration implements WebMvcConfigurer {

    @Setter(onMethod_ = {@Autowired})
    private FastJsonHttpMessageConverter fastJsonHttpMessageConverter;

    @Bean
    @ConditionalOnMissingBean(CharacterEncodingFilter.class)
    public CharacterEncodingFilter characterEncodingFilter() {
        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
        encodingFilter.setEncoding(StandardCharsets.UTF_8.name());
        encodingFilter.setForceEncoding(true);
        return encodingFilter;
    }

    @Bean
    @ConditionalOnMissingBean(FilterRegistrationBean.class)
    public FilterRegistrationBean<SoloFrameworkTraceIdFilter> filterRegistrationBean() {
        FilterRegistrationBean<SoloFrameworkTraceIdFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new SoloFrameworkTraceIdFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }

    @Bean
    @ConditionalOnMissingBean(ISoloFrameworkTraceId.class)
    public ISoloFrameworkTraceId soloFrameworkTraceIdFilter() {
        return new SoloFrameworkTraceIdFilter();
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(0, fastJsonHttpMessageConverter);
    }

}
