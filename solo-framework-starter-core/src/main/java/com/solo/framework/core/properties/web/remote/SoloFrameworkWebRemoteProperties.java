package com.solo.framework.core.properties.web.remote;

import com.solo.framework.core.constant.SoloFrameworkPropertiesPrefixConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
@ConfigurationProperties(prefix = SoloFrameworkPropertiesPrefixConstant.SOLO_FRAMEWORK_WEB_REMOTE_PREFIX)
public class SoloFrameworkWebRemoteProperties {

    /**
     * 是否启用远程客户端自动配置
     */
    private boolean enabled = true;

    /**
     * RestTemplate底层HTTP客户端实现类型: ok_http, http_client
     * 默认: ok_http
     */
    private HttpClientType type = HttpClientType.OK_HTTP;

    /**
     * 是否启用TraceId透传
     */
    private boolean enableTraceIdPropagation = true;

    /**
     * 是否启用请求日志打印
     */
    private boolean enableRequestLogging = true;

    /**
     * 请求日志配置
     */
    @NestedConfigurationProperty
    private RequestLoggingProperties requestLogging = new RequestLoggingProperties();

    /**
     * 连接池配置
     */
    @NestedConfigurationProperty
    private ConnectionPoolProperties connectionPool = new ConnectionPoolProperties();

    /**
     * 超时配置
     */
    @NestedConfigurationProperty
    private TimeoutProperties timeout = new TimeoutProperties();

    /**
     * HTTP客户端类型枚举
     */
    public enum HttpClientType {
        /**
         * OkHttp3 实现（推荐，默认）
         */
        OK_HTTP,
        /**
         * Apache HttpClient 实现
         */
        HTTP_CLIENT
    }

    /**
     * 连接池配置
     */
    @Data
    public static class ConnectionPoolProperties {
        /**
         * 最大连接数
         */
        private int maxTotal = 200;

        /**
         * 每个路由的最大连接数
         */
        private int maxPerRoute = 50;

        /**
         * 连接存活时间（毫秒）
         */
        private long timeToLive = 900000;

        /**
         * 连接池获取连接超时时间（毫秒）
         */
        private int connectionRequestTimeout = 5000;

        /**
         * 空闲连接清理间隔（毫秒）
         */
        private long evictIdleConnections = 60000;
    }

    /**
     * 超时配置
     */
    @Data
    public static class TimeoutProperties {
        /**
         * 连接超时时间（毫秒）
         */
        private int connectTimeout = 5000;

        /**
         * 读取超时时间（毫秒）
         */
        private int readTimeout = 30000;

        /**
         * 写入超时时间（毫秒）- 仅OkHttp支持
         */
        private int writeTimeout = 30000;
    }

    /**
     * 请求日志配置
     */
    @Data
    public static class RequestLoggingProperties {
        /**
         * 响应体最大打印长度（字节）
         * 默认: 2048 (2KB)
         */
        private int maxResponseBodyLength = 2048;
    }

}
