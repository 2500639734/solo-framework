package com.solo.framework.web.util;

import cn.hutool.core.util.StrUtil;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * HTTP工具类
 */
public class HttpUtil {

    private HttpUtil() {

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
            "user-agent", "via", "warning", "origin", "access-control-request-method",
            "access-control-request-headers"
    ));

    /**
     * 二进制文档类型关键字
     */
    public static final Set<String> BINARY_DOCUMENT_SUBTYPES = new HashSet<>(Arrays.asList(
            "pdf", "zip", "rar", "7z", "tar", "gzip",
            "msword", "excel", "powerpoint",
            "openxmlformats", "opendocument"
    ));

    /**
     * 获取自定义请求头
     */
    public static Map<String, String> getCustomHeaders(Map<String, List<String>> headers) {
        Map<String, String> result = new HashMap<>();

        if (headers == null || headers.isEmpty()) {
            return result;
        }

        // 遍历所有请求头，排除HTTP标准请求头
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String headerName = entry.getKey();
            if (headerName == null) {
                continue;
            }

            String lowerHeaderName = headerName.toLowerCase();
            
            // 过滤标准请求头：1. 在标准列表中  2. 以sec-开头的浏览器安全请求头
            if (STANDARD_HTTP_HEADERS.contains(lowerHeaderName) || lowerHeaderName.startsWith("sec-")) {
                continue;
            }

            List<String> values = entry.getValue();
            if (values != null && !values.isEmpty()) {
                result.put(headerName, String.join(",", values));
            }
        }

        return result;
    }

    /**
     * 获取自定义请求头
     * 排除HTTP协议标准请求头，只保留应用自定义的请求头
     */
    public static Map<String, String> getHeaderMap(HttpServletRequest request) {
        Map<String, List<String>> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            Enumeration<String> values = request.getHeaders(key);
            List<String> valueList = new ArrayList<>();
            while (values.hasMoreElements()) {
                valueList.add(values.nextElement());
            }
            headers.put(key.toLowerCase(), valueList);
        }

        return getCustomHeaders(headers);
    }

    /**
     * 获取Http请求参数
     */
    public static Map<String, String> getRequestParams(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();

        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String paramValue = request.getParameter(paramName);
            paramMap.put(paramName, paramValue);
        }

        return paramMap;
    }

    /**
     * 判断是否为二进制Content-Type
     * 显式判断是二进制的情况，其他默认为非二进制（更安全）
     *
     * @param contentType Content-Type字符串
     * @return true-二进制，false-非二进制
     */
    public static boolean isBinaryContentType(String contentType) {
        if (StrUtil.isBlank(contentType)) {
            return false;
        }

        String lowerContentType = contentType.toLowerCase();

        // 图片、视频、音频肯定是二进制
        if (lowerContentType.startsWith("image/") ||
            lowerContentType.startsWith("video/") ||
            lowerContentType.startsWith("audio/")) {
            return true;
        }

        // application/octet-stream 明确的二进制流
        if (lowerContentType.contains("application/octet-stream")) {
            return true;
        }

        // 检查常见的二进制文档类型（PDF、压缩包等）
        if (lowerContentType.startsWith("application/")) {
            for (String binarySubtype : BINARY_DOCUMENT_SUBTYPES) {
                if (lowerContentType.contains(binarySubtype)) {
                    return true;
                }
            }
        }

        // 其他情况默认为非二进制（包括text/*、application/json、application/xml、表单等）
        return false;
    }

    /**
     * 获取请求体内容
     *
     * @param body 请求体字节数组
     * @param contentType Content-Type
     * @return 请求体字符串
     */
    public static String getRequestBody(byte[] body, String contentType) {
        if (body == null || body.length == 0) {
            return "";
        }

        // 检查是否为二进制文件流
        if (isBinaryContentType(contentType)) {
            return "[Binary Data, " + body.length + " bytes]";
        }

        // 转换为字符串
        return new String(body, StandardCharsets.UTF_8);
    }

    /**
     * 提取响应体内容（ClientHttpResponse）
     *
     * @param response ClientHttpResponse对象
     * @param maxLength 最大长度，0表示不限制
     * @return 响应体字符串
     */
    public static String extractResponseBody(ClientHttpResponse response, int maxLength) {
        try {
            MediaType contentType = response.getHeaders().getContentType();
            String contentTypeStr = StrUtil.emptyToDefault(contentType != null ? contentType.toString() : null, null);
            byte[] bodyBytes = StreamUtils.copyToByteArray(response.getBody());
            return extractResponseBody(contentTypeStr, bodyBytes, maxLength);
        } catch (IOException e) {
            return "[Error reading response]";
        }
    }

    /**
     * 提取响应体内容（HttpServletResponse）
     *
     * @param response HttpServletResponse对象
     * @param bodyBytes 响应体字节数组
     * @param maxLength 最大长度，0表示不限制
     * @return 响应体字符串
     */
    public static String extractResponseBody(HttpServletResponse response, byte[] bodyBytes, int maxLength) {
        return extractResponseBody(response.getContentType(), bodyBytes, maxLength);
    }

    /**
     * 提取响应体内容（核心方法）
     *
     * @param contentType Content-Type
     * @param bodyBytes 响应体字节数组
     * @param maxLength 最大长度，0表示不限制
     * @return 响应体字符串
     */
    public static String extractResponseBody(String contentType, byte[] bodyBytes, int maxLength) {
        if (bodyBytes == null || bodyBytes.length == 0) {
            return "";
        }

        // 检查是否为二进制文件流
        if (isBinaryContentType(contentType)) {
            return "[Binary Data, " + bodyBytes.length + " bytes]";
        }

        String bodyStr = new String(bodyBytes, StandardCharsets.UTF_8);
        if (StrUtil.isBlank(bodyStr)) {
            return "";
        }

        // 超长截断
        return truncateIfNeeded(bodyStr, maxLength);
    }

    /**
     * 截断字符串（如果超过最大长度）
     *
     * @param content 原始内容
     * @param maxLength 最大长度，0表示不限制
     * @return 截断后的内容
     */
    private static String truncateIfNeeded(String content, int maxLength) {
        if (maxLength <= 0 || content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "... [已截断, 原始长度: " + content.length() + " 字符]";
    }

    /**
     * 格式化为单行，去除换行符
     *
     * @param content 原始内容
     * @return 单行内容
     */
    public static String formatToSingleLine(String content) {
        if (StrUtil.isBlank(content)) {
            return "{}";
        }
        // 将所有换行符、制表符替换为空格，并压缩多余空格
        return content.replaceAll("[\r\n\t]+", " ").replaceAll(" +", " ").trim();
    }

}
