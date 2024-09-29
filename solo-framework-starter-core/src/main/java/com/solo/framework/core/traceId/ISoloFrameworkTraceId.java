package com.solo.framework.core.traceId;

public interface ISoloFrameworkTraceId {

    /**
     * 获取请求全局追踪id
     * @return 请求全局追踪id
     */
    String getTraceId();

}
