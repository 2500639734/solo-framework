package com.solo.framework.web.trace;

import javax.servlet.http.HttpServletRequest;

public interface ISoloFrameworkTraceId {

    /**
     * 获取请求全局追踪id
     * @return 请求全局追踪id
     */
    String getTraceId(HttpServletRequest request);

}
