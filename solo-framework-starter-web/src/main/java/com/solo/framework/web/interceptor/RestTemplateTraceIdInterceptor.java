package com.solo.framework.web.interceptor;

import cn.hutool.core.util.StrUtil;
import com.solo.framework.core.constant.SoloFrameworkRequestHeaderConstant;
import com.solo.framework.core.context.SoloFrameworkTraceIdContextHolder;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * RestTemplate TraceId透传拦截器
 */
public class RestTemplateTraceIdInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        String traceId = SoloFrameworkTraceIdContextHolder.getTraceId();
        if (StrUtil.isNotBlank(traceId)) {
            request.getHeaders().set(SoloFrameworkRequestHeaderConstant.X_REQUEST_ID, traceId);
        }
        return execution.execute(request, body);
    }

}
