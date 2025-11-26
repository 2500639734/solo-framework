package com.solo.framework.web.interceptor;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.solo.framework.common.enumeration.SoloFrameworkLoggingEnum;
import com.solo.framework.common.util.LogUtil;
import com.solo.framework.core.properties.web.request.SoloFrameworkWebRequestLoggingProperties;
import com.solo.framework.web.annotation.NoRequestLogging;
import com.solo.framework.web.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * HTTP请求日志拦截器, 打印接口请求和响应信息
 */
@Slf4j
public class HttpRequestLoggingInterceptor implements HandlerInterceptor {

    private static final String REQUEST_START_TIME = "requestStartTime";

    private final SoloFrameworkWebRequestLoggingProperties loggingConfig;

    public HttpRequestLoggingInterceptor(SoloFrameworkWebRequestLoggingProperties loggingConfig) {
        this.loggingConfig = loggingConfig;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 检查是否需要排除该URI
        if (shouldExcludeUri(request.getRequestURI())) {
            return true;
        }

        // 检查是否标注了@NoRequestLogging注解
        if (hasNoRequestLoggingAnnotation(handler)) {
            return true;
        }

        request.setAttribute(REQUEST_START_TIME, System.currentTimeMillis());

        // 打印请求日志
        logRequest(request);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 检查是否需要排除该URI
        if (shouldExcludeUri(request.getRequestURI())) {
            return;
        }

        // 检查是否标注了@NoRequestLogging注解
        if (hasNoRequestLoggingAnnotation(handler)) {
            return;
        }

        // 计算耗时
        Long startTime = (Long) request.getAttribute(REQUEST_START_TIME);
        long duration = startTime != null ? System.currentTimeMillis() - startTime : 0;

        // 打印响应日志
        logResponse(request, duration);
    }

    /**
     * 检查URI是否需要排除（不打印日志）
     */
    private boolean shouldExcludeUri(String requestUri) {
        return HttpUtil.matchesExcludeUri(requestUri, loggingConfig.getExcludeUris());
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
         String url = StrUtil.emptyToDefault(request.getRequestURL().toString(), "");
         String method = StrUtil.emptyToDefault(request.getMethod(), "");

         // 是否是json或者表单格式, 如果不是就不打印入参
         String paramsJson = "";
         if (StrUtil.isBlank(request.getContentType()) || HttpUtil.isJsonOrFormContentType(MediaType.parseMediaType(request.getContentType()))) {
             Map<String, String> paramsMap = HttpUtil.getRequestParams(request);
             paramsJson = JSON.toJSONString(paramsMap);
         }

         String headersJsonLog = JSON.toJSONString(HttpUtil.getHeaderMap(request));
         String paramsJsonLog = HttpUtil.formatToSingleLine(paramsJson);

         LogUtil.log("接口请求开始 ======>>> url = {}, method = {}, headers = {}, params = {}", SoloFrameworkLoggingEnum.INFO, url, method, headersJsonLog, paramsJsonLog);
    }

    /**
     * 打印响应日志
     */
    private void logResponse(HttpServletRequest request, long duration) {
        String url = StrUtil.emptyToDefault(request.getRequestURL().toString(), "");
        LogUtil.log("接口请求结束 ======>>> url = {}, duration = {}ms", SoloFrameworkLoggingEnum.INFO, url, duration);
    }

}
