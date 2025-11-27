package com.solo.framework.web.filter;

import com.solo.framework.core.constant.SoloFrameworkRequestHeaderConstant;
import com.solo.framework.core.context.SoloFrameworkTraceIdContextHolder;
import com.solo.framework.web.trace.ISoloFrameworkTraceId;
import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


public class SoloFrameworkTraceIdFilter implements Filter {

    private final ISoloFrameworkTraceId soloFrameworkTraceId;

    public SoloFrameworkTraceIdFilter(ISoloFrameworkTraceId soloFrameworkTraceId) {
        this.soloFrameworkTraceId = soloFrameworkTraceId;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            String traceId = soloFrameworkTraceId.getTraceId((HttpServletRequest) request);
            SoloFrameworkTraceIdContextHolder.setTraceId(traceId);
            MDC.put(SoloFrameworkRequestHeaderConstant.TRACE_ID, traceId);
            chain.doFilter(request, response);
        } finally {
            MDC.remove(SoloFrameworkRequestHeaderConstant.TRACE_ID);
            SoloFrameworkTraceIdContextHolder.removeTraceId();
        }
    }

}
