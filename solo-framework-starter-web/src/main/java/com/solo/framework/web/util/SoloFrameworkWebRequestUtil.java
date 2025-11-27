package com.solo.framework.web.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.solo.framework.web.enums.ErrorCodeEnums;
import com.solo.framework.web.exception.IErrorException;
import com.solo.framework.web.wrapper.BufferingClientHttpResponseWrapper;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class SoloFrameworkWebRequestUtil {

    private SoloFrameworkWebRequestUtil() {

    }

    /**
     * HTTP标准请求头集合，用于过滤
     */
    public static final Set<String> STANDARD_HTTP_HEADERS = CollUtil.newHashSet(
            "accept",
            "accept-charset",
            "accept-encoding",
            "accept-language",
            "authorization",
            "cache-control",
            "connection",
            "content-encoding",
            "content-length",
            "content-type",
            "cookie",
            "date",
            "expect",
            "from",
            "host",
            "if-match",
            "if-modified-since",
            "if-none-match",
            "if-range",
            "if-unmodified-since",
            "max-forwards",
            "pragma",
            "proxy-authorization",
            "range",
            "referer",
            "te",
            "upgrade",
            "user-agent",
            "via",
            "warning",
            "origin",
            "access-control-request-method",
            "access-control-request-headers"
    );

    /**
     * HTTP浏览器或常用第三方携带的标准请求头前缀集合，用于过滤
     */
    private static final Set<String> STANDARD_PREFIXES = CollUtil.newHashSet("sec-", "x-ignored-");

    /**
     * HTTP JSON/FORM子类型集合
     */
    private static final Set<String> HTTP_JSON_FORM_SUBTYPES = CollUtil.newHashSet(
            "json",
            "problem+json",
            "vnd.api+json",
            "ld+json",
            "collection+json",
            "x-www-form-urlencoded",
            "form-data"
    );

    /**
     * 获取自定义请求头
     * - 排除HTTP协议标准请求头，只保留应用自定义的请求头
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
     * 获取自定义请求头
     */
    public static Map<String, String> getCustomHeaders(Map<String, List<String>> headers) {
        Map<String, String> result = new HashMap<>();

        if (CollUtil.isEmpty(headers)) {
            return result;
        }

        return headers.entrySet().stream()
                .filter(e -> Objects.nonNull(e.getKey()))
                .filter(e -> CollUtil.isNotEmpty(e.getValue()))
                .filter(e -> {
                    String lowerKey = e.getKey().toLowerCase();
                    return ! STANDARD_HTTP_HEADERS.contains(lowerKey) && STANDARD_PREFIXES.stream().noneMatch(lowerKey::startsWith);
                })
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> String.join(",", e.getValue())
                ));
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
     * 获取请求体内容
     *
     * @param body 请求体字节数组
     * @param mediaType Content-Type
     * @return 请求体字符串
     */
    public static String getRequestBody(byte[] body, MediaType mediaType) {
        if (body == null || body.length == 0) {
            return "";
        }

        Charset charset = ObjectUtil.defaultIfNull(mediaType.getCharset(), StandardCharsets.UTF_8);
        return new String(body, charset);
    }

    /**
     * 提取响应体内容（BufferingClientHttpResponseWrapper）
     *
     * @param responseWrapper BufferingClientHttpResponseWrapper对象
     * @param maxLength 最大长度，0表示不限制
     * @return 响应体字符串
     */
    public static String extractResponseBody(BufferingClientHttpResponseWrapper responseWrapper, int maxLength) {
        MediaType mediaType = ObjectUtil.defaultIfNull(responseWrapper.getHeaders().getContentType(), MediaType.APPLICATION_JSON);
        Charset charset = ObjectUtil.defaultIfNull(mediaType.getCharset(), StandardCharsets.UTF_8);
        try {
            return truncateIfNeeded(new String(responseWrapper.getBodyBytes(), charset), maxLength);
        } catch (IOException e) {
            throw new IErrorException(ErrorCodeEnums.ERROR.getCode(), "解析响应体失败");
        }
    }

    /**
     * 截断字符串（如果超过最大长度截取）
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
     * @return 去除换行符后的单行内容
     */
    public static String formatToSingleLine(String content) {
        if (StrUtil.isBlank(content)) {
            return "{}";
        }
        // 将所有换行符、制表符替换为空格，并压缩多余空格
        return content.replaceAll("[\r\n\t]+", " ").replaceAll(" +", " ").trim();
    }

    /**
     * 判断URI是否匹配排除列表中的任一模式
     *
     * @param uri 请求URI
     * @param excludeUris 排除URI列表（支持通配符/**）
     * @return true-匹配成功需要排除，false-不匹配
     */
    public static boolean matchesExcludeUri(String uri, List<String> excludeUris) {
        if (StrUtil.isBlank(uri) || excludeUris == null || excludeUris.isEmpty()) {
            return false;
        }

        return excludeUris.stream().anyMatch(pattern -> matchesUriPattern(uri, pattern));
    }

    /**
     * 匹配URI模式（支持通配符）
     *
     * @param uri 请求URI
     * @param pattern 匹配模式（支持/**通配符）
     * @return true-匹配，false-不匹配
     */
    private static boolean matchesUriPattern(String uri, String pattern) {
        if (StrUtil.isBlank(pattern)) {
            return false;
        }

        // 精确匹配
        if (uri.equals(pattern)) {
            return true;
        }

        // 通配符匹配（如 /swagger-ui/**）
        if (pattern.endsWith("/**")) {
            String prefix = pattern.substring(0, pattern.length() - 2);
            return uri.startsWith(prefix);
        }

        return false;
    }

    /**
     * 判断请求或响应格式是否为JSON/FORM表单
     */
    public static boolean isJsonOrFormContentType(MediaType mediaType) {
        if (Objects.isNull(mediaType)) {
            return false;
        }

        String subtype = mediaType.getSubtype().toLowerCase();
        return HTTP_JSON_FORM_SUBTYPES.contains(subtype) || subtype.endsWith("+json");
    }

}
