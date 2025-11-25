package com.solo.framework.web.interceptor;

import cn.hutool.core.util.StrUtil;
import com.solo.framework.core.constant.SoloFrameworkRequestHeaderConstant;
import com.solo.framework.core.context.SoloFrameworkTraceIdContextHolder;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.UUID;

/**
 * RestTemplate TraceId透传拦截器
 */
public class RestTemplateTraceIdInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        // 获取traceId, 如果为空就生成一个
        String traceId = SoloFrameworkTraceIdContextHolder.getTraceId();
        if (StrUtil.isBlank(traceId)) {
            traceId = UUID.randomUUID().toString();
        }
        request.getHeaders().set(SoloFrameworkRequestHeaderConstant.X_REQUEST_ID, traceId);
        return execution.execute(request, body);
    }

}
