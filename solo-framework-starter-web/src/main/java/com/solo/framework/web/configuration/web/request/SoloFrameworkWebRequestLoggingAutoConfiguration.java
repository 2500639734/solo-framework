package com.solo.framework.web.configuration.web.request;

import com.solo.framework.common.enumeration.SoloFrameworkLoggingEnum;
import com.solo.framework.common.util.LogUtil;
import com.solo.framework.core.properties.web.SoloFrameworkWebProperties;
import com.solo.framework.web.interceptor.HttpRequestLoggingInterceptor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * HTTP请求日志自动配置
 */
@Slf4j
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(
        prefix = "solo.framework.web.request-logging",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@EnableConfigurationProperties(SoloFrameworkWebProperties.class)
public class SoloFrameworkWebRequestLoggingAutoConfiguration implements WebMvcConfigurer {

    @Setter(onMethod_ = {@Autowired})
    private SoloFrameworkWebProperties webProperties;

    /**
     * 注册HTTP请求日志拦截器
     * 设置为最高优先级，确保前置处理第一个执行，后置处理最后一个执行
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HttpRequestLoggingInterceptor(webProperties.getRequestLogging()))
                .addPathPatterns("/**")
                .order(Ordered.HIGHEST_PRECEDENCE);
        LogUtil.log("HTTP请求日志拦截器已启用", SoloFrameworkLoggingEnum.INFO);
    }

}
