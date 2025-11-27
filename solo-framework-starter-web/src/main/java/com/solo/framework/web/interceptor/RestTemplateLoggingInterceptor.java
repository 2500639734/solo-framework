package com.solo.framework.web.interceptor;

import com.alibaba.fastjson2.JSON;
import com.solo.framework.common.enumeration.SoloFrameworkLoggingEnum;
import com.solo.framework.common.util.LogUtil;
import com.solo.framework.core.properties.web.remote.SoloFrameworkWebRemoteProperties;
import com.solo.framework.web.enums.ErrorCodeEnums;
import com.solo.framework.web.exception.IErrorException;
import com.solo.framework.web.util.SoloFrameworkWebRequestUtil;
import com.solo.framework.web.wrapper.BufferingClientHttpResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.util.Map;
import java.util.Objects;

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
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
        ClientHttpResponse response;
        try {
            long startTime = System.currentTimeMillis();

            // 打印请求日志
            logRequest(request, body);

            // 执行请求
            response = execution.execute(request, body);

            // 打印响应日志
            response = logResponse(request, response, System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            throw new IErrorException(ErrorCodeEnums.ERROR_REQUEST_REQUEST_FAIL, e);
        }
        return response;
    }

    /**
     * 打印请求日志
     */
    private void logRequest(HttpRequest request, byte[] body) {
        String url = request.getURI().toString();
        String method = Objects.requireNonNull(request.getMethod()).name();
        Map<String, String> headers = SoloFrameworkWebRequestUtil.getCustomHeaders(request.getHeaders());

        // 是否是json或者表单格式, 如果不是就不打印入参
        MediaType mediaType = request.getHeaders().getContentType();
        String paramsJson = "";
        if (Objects.isNull(mediaType) || SoloFrameworkWebRequestUtil.isJsonOrFormContentType(mediaType)) {
            paramsJson = SoloFrameworkWebRequestUtil.getRequestBody(body, mediaType);
        }

        LogUtil.log("接口远程调用开始, url = {}, method = {}, headers = {}, params = {}", SoloFrameworkLoggingEnum.INFO, url, method, JSON.toJSONString(headers), SoloFrameworkWebRequestUtil.formatToSingleLine(paramsJson));
    }

    /**
     * 打印响应日志
     */
    private ClientHttpResponse logResponse(HttpRequest request, ClientHttpResponse response, long duration) {
        String url = request.getURI().toString();

        // 是否是json或者表单格式, 如果不是就不打印返参
        String responseBody = "";
        MediaType mediaType = response.getHeaders().getContentType();
        if (Objects.nonNull(mediaType) && SoloFrameworkWebRequestUtil.isJsonOrFormContentType(mediaType)) {
            BufferingClientHttpResponseWrapper responseWrapper = new BufferingClientHttpResponseWrapper(response);
            responseBody = SoloFrameworkWebRequestUtil.extractResponseBody(responseWrapper, loggingConfig.getMaxResponseBodyLength());
            response = responseWrapper;
        }

        LogUtil.log("接口远程调用结束, url = {}, response = {}, duration = {}ms", SoloFrameworkLoggingEnum.INFO, url, SoloFrameworkWebRequestUtil.formatToSingleLine(responseBody), duration);

        return response;
    }

}
