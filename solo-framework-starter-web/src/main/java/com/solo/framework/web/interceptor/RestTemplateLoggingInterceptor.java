package com.solo.framework.web.interceptor;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.solo.framework.common.enumeration.SoloFrameworkLoggingEnum;
import com.solo.framework.common.util.HttpUtil;
import com.solo.framework.common.util.LogUtil;
import com.solo.framework.core.properties.web.remote.SoloFrameworkWebRemoteProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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

        ClientHttpResponse response = execution.execute(request, body);

        long endTime = System.currentTimeMillis();

        // 将响应包装成可重复读取的形式，解决流读取后无法重复使用的问题
        BufferingClientHttpResponseWrapper responseWrapper = new BufferingClientHttpResponseWrapper(response);

        // 打印响应日志
        logResponse(request, responseWrapper, endTime - startTime);

        return responseWrapper;
    }

    /**
     * 打印请求日志
     */
    private void logRequest(HttpRequest request, byte[] body) {
        try {
            String url = request.getURI().toString();
            Map<String, String> headers = HttpUtil.extractCustomHeaders(request.getHeaders());
            String params = extractRequestBody(body, request.getHeaders());

            LogUtil.log("接口远程调用开始, url = {}, headers = {}, params = {}", SoloFrameworkLoggingEnum.INFO, url, JSON.toJSONString(headers), HttpUtil.formatToSingleLine(params));
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
            int status = response.getRawStatusCode();
            String responseBody = extractResponseBody(response);

            LogUtil.log("接口远程调用结束, url = {}, status = {}, response = {}, duration = {}ms", SoloFrameworkLoggingEnum.INFO, url, status, HttpUtil.formatToSingleLine(responseBody), duration);
        } catch (Exception e) {
            LogUtil.log("打印响应日志失败", SoloFrameworkLoggingEnum.WARN, e);
        }
    }

    /**
     * 提取请求体
     */
    private String extractRequestBody(byte[] body, HttpHeaders headers) {
        if (body == null || body.length == 0) {
            return "";
        }

        // 检查是否为二进制文件流
        MediaType contentType = headers.getContentType();
        if (contentType != null && HttpUtil.isBinaryContentType(contentType.toString())) {
            return "[Binary Data, " + body.length + " bytes]";
        }

        // 转换为字符串，全部打印不截断
        return new String(body, StandardCharsets.UTF_8);
    }

    /**
     * 提取响应体
     * 使用StreamUtils复制流，避免读取后无法重复使用
     */
    private String extractResponseBody(ClientHttpResponse response) {
        try {
            // 检查是否为二进制文件流
            MediaType contentType = response.getHeaders().getContentType();
            if (contentType != null && HttpUtil.isBinaryContentType(contentType.toString())) {
                return "[Binary Data]";
            }

            // 使用StreamUtils读取响应体（已经由BufferingClientHttpResponseWrapper缓存）
            byte[] bodyBytes = StreamUtils.copyToByteArray(response.getBody());
            if (bodyBytes == null || bodyBytes.length == 0) {
                return "";
            }

            String bodyStr = new String(bodyBytes, StandardCharsets.UTF_8);
            if (StrUtil.isBlank(bodyStr)) {
                return "";
            }

            // 超长截断打印
            int maxLength = loggingConfig.getMaxResponseBodyLength();
            if (bodyStr.length() > maxLength) {
                return bodyStr.substring(0, maxLength) + "... [已截断, 原始长度: " + bodyStr.length() + " 字符]";
            }

            return bodyStr;
        } catch (Exception e) {
            LogUtil.log("读取响应体失败", SoloFrameworkLoggingEnum.WARN, e);
            return "[Error reading response]";
        }
    }

    /**
     * 响应包装类，用于缓存响应体，允许多次读取
     */
    private static class BufferingClientHttpResponseWrapper implements ClientHttpResponse {
        private final ClientHttpResponse response;
        private byte[] body;

        public BufferingClientHttpResponseWrapper(ClientHttpResponse response) {
            this.response = response;
        }

        @Override
        public HttpHeaders getHeaders() {
            return this.response.getHeaders();
        }

        @Override
        public InputStream getBody() throws IOException {
            if (this.body == null) {
                this.body = StreamUtils.copyToByteArray(this.response.getBody());
            }
            return new ByteArrayInputStream(this.body);
        }

        @Override
        public int getRawStatusCode() throws IOException {
            return this.response.getRawStatusCode();
        }

        @Override
        public String getStatusText() throws IOException {
            return this.response.getStatusText();
        }

        @Override
        public void close() {
            this.response.close();
        }

        @Override
        public org.springframework.http.HttpStatus getStatusCode() throws IOException {
            return this.response.getStatusCode();
        }
    }

}
