package com.solo.framework.common.constant;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * HTTP相关常量
 */
public class HttpConstant {

    private HttpConstant() {
    }

    /**
     * HTTP标准请求头集合，用于过滤
     */
    public static final Set<String> STANDARD_HTTP_HEADERS = new HashSet<>(Arrays.asList(
            "accept", "accept-charset", "accept-encoding", "accept-language",
            "authorization", "cache-control", "connection", "content-encoding",
            "content-length", "content-type", "cookie", "date", "expect",
            "from", "host", "if-match", "if-modified-since", "if-none-match",
            "if-range", "if-unmodified-since", "max-forwards", "pragma",
            "proxy-authorization", "range", "referer", "te", "upgrade",
            "user-agent", "via", "warning"
    ));

    /**
     * 二进制文档类型关键字
     */
    public static final Set<String> BINARY_DOCUMENT_SUBTYPES = new HashSet<>(Arrays.asList(
            "pdf", "zip", "rar", "7z", "tar", "gzip",
            "msword", "excel", "powerpoint",
            "openxmlformats", "opendocument"
    ));

}
