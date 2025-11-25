package com.solo.framework.web.interceptor;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.solo.framework.common.enumeration.SoloFrameworkLoggingEnum;
import com.solo.framework.core.properties.web.request.SoloFrameworkWebRequestLoggingProperties;
import com.solo.framework.web.util.HttpUtil;
import com.solo.framework.common.util.LogUtil;
import com.solo.framework.web.annotation.NoRequestLogging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * HTTP请求日志拦截器, 打印接口请求和响应信息
 */
@Slf4j
public class HttpRequestLoggingInterceptor implements HandlerInterceptor {

    private static final String START_TIME_ATTRIBUTE = "REQUEST_START_TIME";

    private final SoloFrameworkWebRequestLoggingProperties loggingConfig;

    public HttpRequestLoggingInterceptor(SoloFrameworkWebRequestLoggingProperties loggingConfig) {
        this.loggingConfig = loggingConfig;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 检查是否标注了@NoRequestLogging注解
        if (hasNoRequestLoggingAnnotation(handler)) {
            return true;
        }

        // 记录请求开始时间
        request.setAttribute(START_TIME_ATTRIBUTE, System.currentTimeMillis());

        // 打印请求日志
        logRequest(request);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 检查是否标注了@NoRequestLogging注解
        if (hasNoRequestLoggingAnnotation(handler)) {
            return;
        }

        // 计算耗时
        Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        long duration = startTime != null ? System.currentTimeMillis() - startTime : 0;

        // 打印响应日志
        logResponse(request, response, duration);
    }

    /**
     * 检查是否标注了@NoRequestLogging注解
     */
    private boolean hasNoRequestLoggingAnnotation(Object handler) {
        if (! (handler instanceof HandlerMethod)) {
            return false;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // 检查方法上是否有注解
        if (handlerMethod.hasMethodAnnotation(NoRequestLogging.class)) {
            return true;
        }

        // 检查类上是否有注解
        return handlerMethod.getBeanType().isAnnotationPresent(NoRequestLogging.class);
    }

    /**
     * 打印请求日志
     */
     private void logRequest(HttpServletRequest request) {
        try {
            String url = StrUtil.emptyToDefault(request.getRequestURL().toString(), "");
            String method = StrUtil.emptyToDefault(request.getMethod(), "");
            Map<String, String> headers = HttpUtil.getHeaderMap(request);
            Map<String, String> paramMap = HttpUtil.getRequestParams(request);
            LogUtil.log("接口请求开始 ======>>> url = {}, method = {}, headers = {}, params = {}", SoloFrameworkLoggingEnum.INFO, url, method, JSON.toJSONString(headers), HttpUtil.formatToSingleLine(JSON.toJSONString(paramMap)));
        } catch (Exception e) {
            LogUtil.log("打印接口请求日志失败", SoloFrameworkLoggingEnum.WARN, e);
        }
    }

    /**
     * 打印响应日志
     */
    private void logResponse(HttpServletRequest request, HttpServletResponse response, long duration) {
        try {
            String url = StrUtil.emptyToDefault(request.getRequestURL().toString(), "");
            
            // 提取响应体（如果是ContentCachingResponseWrapper）
            String responseBody = "";
            if (response instanceof ContentCachingResponseWrapper) {
                ContentCachingResponseWrapper wrapper = (ContentCachingResponseWrapper) response;
                byte[] content = wrapper.getContentAsByteArray();
                responseBody = HttpUtil.extractResponseBody(response, content, loggingConfig.getMaxResponseBodyLength());
            }

            LogUtil.log("接口请求结束 ======>>> url = {}, response = {}, duration = {}ms", SoloFrameworkLoggingEnum.INFO, url, HttpUtil.formatToSingleLine(responseBody), duration);
        } catch (Exception e) {
            LogUtil.log("打印接口响应日志失败", SoloFrameworkLoggingEnum.WARN, e);
        }
    }

}
