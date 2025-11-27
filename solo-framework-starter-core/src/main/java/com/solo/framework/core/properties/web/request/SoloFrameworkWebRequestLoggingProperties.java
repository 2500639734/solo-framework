package com.solo.framework.core.properties.web.request;

import lombok.Data;

import java.util.Arrays;
import java.util.List;

/**
 * HTTP请求日志配置
 */
@Data
public class SoloFrameworkWebRequestLoggingProperties {

    /**
     * 是否启用HTTP请求日志
     * 默认: true
     */
    private boolean enabled = true;

    /**
     * 是否打印用户信息（code、name）
     * 默认: true
     */
    private boolean logUserInfo = true;

    /**
     * 需要排除的URI路径列表（不打印日志）
     * 默认排除Swagger、Knife4j、error及favicon.ico等系统路径
     */
    private List<String> excludeUris = Arrays.asList(
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/swagger-resources",
            "/v2/api-docs",
            "/v3/api-docs",
            "/doc.html",
            "/webjars/**",
            "/favicon.ico",
            "/error"
    );

}
