package com.solo.framework.web.configuration.web;

import com.alibaba.fastjson2.support.spring.http.converter.FastJsonHttpMessageConverter;
import com.solo.framework.core.constant.SoloFrameworkPropertiesPrefixConstant;
import com.solo.framework.core.properties.web.SoloFrameworkWebProperties;
import com.solo.framework.web.trace.ISoloFrameworkTraceId;
import com.solo.framework.web.trace.DefaultSoloFrameworkTraceId;
import com.solo.framework.web.user.DefaultSoloFrameworkUser;
import com.solo.framework.core.user.ISoloFrameworkUser;
import com.solo.framework.web.filter.SoloFrameworkTraceIdFilter;
import com.solo.framework.web.filter.SoloFrameworkUserFilter;
import lombok.Setter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
import java.util.Objects;

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

    /**
     * 默认用户信息实现
     */
    @Bean
    @ConditionalOnMissingBean(ISoloFrameworkUser.class)
    @ConditionalOnProperty(name = SoloFrameworkPropertiesPrefixConstant.SOLO_FRAMEWORK_WEB_USER_PREFIX + ".enabled", havingValue = "true", matchIfMissing = true)
    public ISoloFrameworkUser defaultSoloFrameworkUser() {
        return new DefaultSoloFrameworkUser();
    }

    /**
     * 用户信息过滤器注册, 优先级高于 TraceId 过滤器, 确保用户信息先被设置
     */
    @Bean
    @ConditionalOnProperty(name = SoloFrameworkPropertiesPrefixConstant.SOLO_FRAMEWORK_WEB_USER_PREFIX + ".enabled", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<SoloFrameworkUserFilter> userFilterRegistrationBean(ObjectProvider<ISoloFrameworkUser> soloFrameworkUserProvider) {
        FilterRegistrationBean<SoloFrameworkUserFilter> registrationBean = new FilterRegistrationBean<>();
        ISoloFrameworkUser soloFrameworkUser = soloFrameworkUserProvider.getIfAvailable();
        if (Objects.nonNull(soloFrameworkUser)) {
            registrationBean.setFilter(new SoloFrameworkUserFilter(soloFrameworkUser));
            registrationBean.addUrlPatterns("/*");
            registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        }
        return registrationBean;
    }

    /**
     * 默认TraceId实现
     */
    @Bean
    @ConditionalOnMissingBean(ISoloFrameworkTraceId.class)
    @ConditionalOnProperty(name = SoloFrameworkPropertiesPrefixConstant.SOLO_FRAMEWORK_WEB_TRACE_PREFIX + ".enabled", havingValue = "true", matchIfMissing = true)
    public ISoloFrameworkTraceId defaultSoloFrameworkTraceId() {
        return new DefaultSoloFrameworkTraceId();
    }

    /**
     * TraceId过滤器注册
     */
    @Bean
    @ConditionalOnProperty(name = SoloFrameworkPropertiesPrefixConstant.SOLO_FRAMEWORK_WEB_TRACE_PREFIX + ".enabled", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<SoloFrameworkTraceIdFilter> traceIdFilterRegistrationBean(ObjectProvider<ISoloFrameworkTraceId> soloFrameworkTraceIdProvider) {
        FilterRegistrationBean<SoloFrameworkTraceIdFilter> registrationBean = new FilterRegistrationBean<>();
        ISoloFrameworkTraceId soloFrameworkTraceId = soloFrameworkTraceIdProvider.getIfAvailable();
        if (Objects.nonNull(soloFrameworkTraceId)) {
            registrationBean.setFilter(new SoloFrameworkTraceIdFilter(soloFrameworkTraceId));
            registrationBean.addUrlPatterns("/*");
            registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        }
        return registrationBean;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(0, fastJsonHttpMessageConverter);
    }

}
