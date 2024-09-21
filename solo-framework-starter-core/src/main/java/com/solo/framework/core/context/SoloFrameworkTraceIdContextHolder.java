package com.solo.framework.core.context;

import com.alibaba.ttl.TransmittableThreadLocal;

public class SoloFrameworkTraceIdContextHolder {

    private static final TransmittableThreadLocal<String> traceIdThreadLocal = new TransmittableThreadLocal<>();

    public static void setTraceId(String traceId) {
        traceIdThreadLocal.set(traceId);
    }

    public static String getTraceId() {
        return traceIdThreadLocal.get();
    }

    public static void removeTraceId() {
        traceIdThreadLocal.remove();
    }

}
