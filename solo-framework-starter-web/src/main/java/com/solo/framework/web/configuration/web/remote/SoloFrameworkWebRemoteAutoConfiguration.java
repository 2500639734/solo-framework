package com.solo.framework.web.configuration.web.remote;

import com.solo.framework.common.enumeration.SoloFrameworkLoggingEnum;
import com.solo.framework.common.util.LogUtil;
import com.solo.framework.core.properties.web.remote.SoloFrameworkWebRemoteProperties;
import com.solo.framework.web.interceptor.RestTemplateLoggingInterceptor;
import com.solo.framework.web.interceptor.RestTemplateTraceIdInterceptor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Solo Framework 远程调用自动配置
 * 统一使用RestTemplate，支持OkHttp（默认）和Apache HttpClient两种实现
 */
@Slf4j
@Configuration
@ConditionalOnClass(RestTemplate.class)
@ConditionalOnProperty(
        prefix = "solo.framework.web.remote",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@EnableConfigurationProperties(SoloFrameworkWebRemoteProperties.class)
public class SoloFrameworkWebRemoteAutoConfiguration {

    @Setter(onMethod_ = {@Autowired})
    private SoloFrameworkWebRemoteProperties webRemoteProperties;

    /******************************************* OkHttp3 配置（默认） *******************************************/

    /**
     * 配置OkHttp连接池
     */
    @Bean
    @ConditionalOnClass(name = "okhttp3.OkHttpClient")
    @ConditionalOnMissingBean(ConnectionPool.class)
    @ConditionalOnProperty(prefix = "solo.framework.web.remote", name = "type", havingValue = "OK_HTTP", matchIfMissing = true)
    public ConnectionPool okHttpConnectionPool() {
        SoloFrameworkWebRemoteProperties.ConnectionPoolProperties pool = webRemoteProperties.getConnectionPool();
        return new ConnectionPool(pool.getMaxTotal(), pool.getTimeToLive(), TimeUnit.MILLISECONDS);
    }

    /**
     * 配置OkHttpClient
     */
    @Bean
    @ConditionalOnClass(name = "okhttp3.OkHttpClient")
    @ConditionalOnMissingBean(OkHttpClient.class)
    @ConditionalOnProperty(prefix = "solo.framework.web.remote", name = "type", havingValue = "OK_HTTP", matchIfMissing = true)
    public OkHttpClient okHttpClient(ObjectProvider<ConnectionPool> connectionPoolProvider) {
        SoloFrameworkWebRemoteProperties.TimeoutProperties timeout = webRemoteProperties.getTimeout();

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                // 连接超时
                .connectTimeout(timeout.getConnectTimeout(), TimeUnit.MILLISECONDS)
                // 读取超时
                .readTimeout(timeout.getReadTimeout(), TimeUnit.MILLISECONDS)
                // 写入超时
                .writeTimeout(timeout.getWriteTimeout(), TimeUnit.MILLISECONDS)
                // 连接失败时重试
                .retryOnConnectionFailure(true);

        // 设置连接池
        ConnectionPool connectionPool = connectionPoolProvider.getIfAvailable();
        if (connectionPool != null) {
            builder.connectionPool(connectionPool);
        }

        return builder.build();
    }

    /**
     * 配置OkHttp3ClientHttpRequestFactory
     */
    @Bean
    @ConditionalOnClass(name = "okhttp3.OkHttpClient")
    @ConditionalOnMissingBean(ClientHttpRequestFactory.class)
    @ConditionalOnProperty(prefix = "solo.framework.web.remote", name = "type", havingValue = "OK_HTTP", matchIfMissing = true)
    public ClientHttpRequestFactory okHttp3ClientHttpRequestFactory(OkHttpClient okHttpClient) {
        OkHttp3ClientHttpRequestFactory factory = new OkHttp3ClientHttpRequestFactory(okHttpClient);
        factory.setConnectTimeout(webRemoteProperties.getTimeout().getConnectTimeout());
        factory.setReadTimeout(webRemoteProperties.getTimeout().getReadTimeout());
        factory.setWriteTimeout(webRemoteProperties.getTimeout().getWriteTimeout());
        return factory;
    }

    /******************************************* Http Client 配置 *******************************************/
    /**
     * 配置Apache HttpClient连接池
     */
    @Bean
    @ConditionalOnClass(name = "org.apache.http.client.HttpClient")
    @ConditionalOnProperty(prefix = "solo.framework.web.remote", name = "type", havingValue = "HTTP_CLIENT")
    @ConditionalOnMissingBean(PoolingHttpClientConnectionManager.class)
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
                webRemoteProperties.getConnectionPool().getTimeToLive(),
                TimeUnit.MILLISECONDS
        );

        SoloFrameworkWebRemoteProperties.ConnectionPoolProperties pool = webRemoteProperties.getConnectionPool();
        // 最大连接数
        connectionManager.setMaxTotal(pool.getMaxTotal());
        // 每个路由的最大连接数
        connectionManager.setDefaultMaxPerRoute(pool.getMaxPerRoute());

        return connectionManager;
    }

    /**
     * 配置Apache HttpClient
     */
    @Bean
    @ConditionalOnClass(name = "org.apache.http.client.HttpClient")
    @ConditionalOnProperty(prefix = "solo.framework.web.remote", name = "type", havingValue = "HTTP_CLIENT")
    @ConditionalOnMissingBean(CloseableHttpClient.class)
    public CloseableHttpClient httpClient(ObjectProvider<PoolingHttpClientConnectionManager> connectionManagerProvider) {
        SoloFrameworkWebRemoteProperties.TimeoutProperties timeout = webRemoteProperties.getTimeout();
        SoloFrameworkWebRemoteProperties.ConnectionPoolProperties pool = webRemoteProperties.getConnectionPool();

        RequestConfig requestConfig = RequestConfig.custom()
                // 连接超时时间
                .setConnectTimeout(timeout.getConnectTimeout())
                // 读取超时时间
                .setSocketTimeout(timeout.getReadTimeout())
                // 从连接池获取连接超时时间
                .setConnectionRequestTimeout(pool.getConnectionRequestTimeout())
                .build();

        HttpClientBuilder builder = HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                // 设置空闲连接清理
                .evictIdleConnections(pool.getEvictIdleConnections(), TimeUnit.MILLISECONDS);

        // 设置连接池
        PoolingHttpClientConnectionManager connectionManager = connectionManagerProvider.getIfAvailable();
        if (connectionManager != null) {
            builder.setConnectionManager(connectionManager);
        }

        return builder.build();
    }

    /**
     * 配置HttpComponentsClientHttpRequestFactory
     */
    @Bean
    @ConditionalOnClass(name = "org.apache.http.client.HttpClient")
    @ConditionalOnProperty(prefix = "solo.framework.web.remote", name = "type", havingValue = "HTTP_CLIENT")
    @ConditionalOnMissingBean(ClientHttpRequestFactory.class)
    public ClientHttpRequestFactory httpComponentsClientHttpRequestFactory(CloseableHttpClient httpClient) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        factory.setConnectTimeout(webRemoteProperties.getTimeout().getConnectTimeout());
        factory.setReadTimeout(webRemoteProperties.getTimeout().getReadTimeout());
        factory.setConnectionRequestTimeout(webRemoteProperties.getConnectionPool().getConnectionRequestTimeout());
        return factory;
    }

    /******************************************* RestTemplate 统一配置 *******************************************/

    /**
     * 配置RestTemplate
     * 统一使用RestTemplate API，底层根据配置自动选择实现
     */
    @Bean
    @ConditionalOnMissingBean(RestTemplate.class)
    public RestTemplate restTemplate(ClientHttpRequestFactory factory, ObjectProvider<List<ClientHttpRequestInterceptor>> customInterceptorsProvider) {
        RestTemplate restTemplate = new RestTemplate(factory);

        // 收集所有拦截器
        List<ClientHttpRequestInterceptor> allInterceptors = new ArrayList<>();

        // 1. 添加用户自定义拦截器
        List<ClientHttpRequestInterceptor> customInterceptors = customInterceptorsProvider.getIfAvailable();
        if (customInterceptors != null && !customInterceptors.isEmpty()) {
            allInterceptors.addAll(customInterceptors);
        }

        // 2. 添加TraceId透传拦截器（统一在RestTemplate层面实现）
        if (webRemoteProperties.isEnableTraceIdPropagation()) {
            allInterceptors.add(new RestTemplateTraceIdInterceptor());
        }

        // 3. 添加请求日志拦截器
        if (webRemoteProperties.isEnableRequestLogging()) {
            allInterceptors.add(new RestTemplateLoggingInterceptor(webRemoteProperties.getRequestLogging()));
        }

        restTemplate.setInterceptors(allInterceptors);

        LogUtil.log("内置RestTemplate配置完成，底层实现: {}, TraceId透传: {}, 请求日志: {}, 自定义拦截器数量: {}", SoloFrameworkLoggingEnum.INFO,
                webRemoteProperties.getType(),
                webRemoteProperties.isEnableTraceIdPropagation(),
                webRemoteProperties.isEnableRequestLogging(),
                customInterceptors != null ? customInterceptors.size() : 0);

        return restTemplate;
    }

}
