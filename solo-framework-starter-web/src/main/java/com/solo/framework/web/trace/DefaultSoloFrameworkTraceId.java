package com.solo.framework.web.trace;

import cn.hutool.core.util.StrUtil;
import com.solo.framework.core.constant.SoloFrameworkRequestHeaderConstant;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

public class DefaultSoloFrameworkTraceId implements ISoloFrameworkTraceId {

    /**
     * 获取traceId, 如果为空就生成一个
     */
    @Override
    public String getTraceId(HttpServletRequest request) {
        String traceId = request.getHeader(SoloFrameworkRequestHeaderConstant.X_REQUEST_ID);
        if (StrUtil.isBlank(traceId)) {
            traceId = UUID.randomUUID().toString();
        }
        return traceId;
    }

}
