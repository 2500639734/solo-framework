package com.solo.framework.web.filter;

import cn.hutool.core.util.StrUtil;
import com.solo.framework.core.constant.SoloFrameworkRequestHeaderConstant;
import com.solo.framework.core.context.SoloFrameworkTraceIdContextHolder;
import com.solo.framework.core.traceId.ISoloFrameworkTraceId;
import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;


public class SoloFrameworkTraceIdFilter implements ISoloFrameworkTraceId, Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            String traceId = getTraceIdIfBlankThenCreated((HttpServletRequest) request);
            SoloFrameworkTraceIdContextHolder.setTraceId(traceId);
            MDC.put(SoloFrameworkRequestHeaderConstant.TRACE_ID, traceId);
            chain.doFilter(request, response);
        } finally {
            MDC.remove(SoloFrameworkRequestHeaderConstant.TRACE_ID);
            SoloFrameworkTraceIdContextHolder.removeTraceId();
        }
    }

    @Override
    public String getTraceId() {
        return SoloFrameworkTraceIdContextHolder.getTraceId();
    }

    /**
     * 获取traceId, 如果为空就生成一个
     * @param httpRequest http请求对象
     * @return traceId
     */
    private static String getTraceIdIfBlankThenCreated(HttpServletRequest httpRequest) {
        String traceId = httpRequest.getHeader(SoloFrameworkRequestHeaderConstant.X_REQUEST_ID);
        if (StrUtil.isBlank(traceId)) {
            traceId = UUID.randomUUID().toString();
        }
        return traceId;
    }

}
