package com.solo.framework.web.interceptor;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.solo.framework.common.enumeration.SoloFrameworkLoggingEnum;
import com.solo.framework.common.util.LogUtil;
import com.solo.framework.core.properties.web.remote.SoloFrameworkWebRemoteProperties;
import com.solo.framework.web.util.HttpUtil;
import com.solo.framework.web.wrapper.BufferingClientHttpResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.Map;

/**
 * RestTemplate请求日志拦截器
 * 自动打印远程调用的请求和响应信息
 */
@Slf4j
public class RestTemplateLoggingInterceptor implements ClientHttpRequestInterceptor {

    private final SoloFrameworkWebRemoteProperties.RequestLoggingProperties loggingConfig;

    public RestTemplateLoggingInterceptor(SoloFrameworkWebRemoteProperties.RequestLoggingProperties loggingConfig) {
        this.loggingConfig = loggingConfig;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        long startTime = System.currentTimeMillis();

        // 打印请求日志
        logRequest(request, body);

        // 执行请求
        ClientHttpResponse response = execution.execute(request, body);

        // 将响应包装成可重复读取的形式
        BufferingClientHttpResponseWrapper responseWrapper = new BufferingClientHttpResponseWrapper(response);

        // 打印响应日志
        logResponse(request, responseWrapper, System.currentTimeMillis() - startTime);

        return responseWrapper;
    }

    /**
     * 打印请求日志
     */
    private void logRequest(HttpRequest request, byte[] body) {
        try {
            String url = StrUtil.emptyToDefault(request.getURI().toString(), "");
            String method = StrUtil.emptyToDefault(request.getMethod().name(), "");
            Map<String, String> headers = HttpUtil.getCustomHeaders(request.getHeaders());
            MediaType contentType = request.getHeaders().getContentType();
            String contentTypeStr = StrUtil.emptyToDefault(contentType != null ? contentType.toString() : null, null);
            String params = HttpUtil.getRequestBody(body, contentTypeStr);

            LogUtil.log("接口远程调用开始, url = {}, method = {}, headers = {}, params = {}", SoloFrameworkLoggingEnum.INFO, url, method, JSON.toJSONString(headers), HttpUtil.formatToSingleLine(params));
        } catch (Exception e) {
            LogUtil.log("打印请求日志失败", SoloFrameworkLoggingEnum.WARN, e);
        }
    }

    /**
     * 打印响应日志
     */
    private void logResponse(HttpRequest request, ClientHttpResponse response, long duration) {
        try {
            String url = request.getURI().toString();
            String responseBody = HttpUtil.extractResponseBody(response, loggingConfig.getMaxResponseBodyLength());

            LogUtil.log("接口远程调用结束, url = {}, response = {}, duration = {}ms", SoloFrameworkLoggingEnum.INFO, url, HttpUtil.formatToSingleLine(responseBody), duration);
        } catch (Exception e) {
            LogUtil.log("打印接口远程调用响应日志失败", SoloFrameworkLoggingEnum.WARN, e);
        }
    }

}
