package com.solo.framework.common.util;

import cn.hutool.core.util.StrUtil;
import com.solo.framework.common.constant.HttpConstant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP工具类
 */
public class HttpUtil {

    private HttpUtil() {
    }

    /**
     * 提取自定义请求头
     * 排除HTTP协议标准请求头，只保留应用自定义的请求头
     *
     * @param headers 所有请求头
     * @return 自定义请求头Map
     */
    public static Map<String, String> extractCustomHeaders(Map<String, List<String>> headers) {
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

            // 不是HTTP标准请求头，则为自定义请求头
            if (!HttpConstant.STANDARD_HTTP_HEADERS.contains(headerName.toLowerCase())) {
                List<String> values = entry.getValue();
                if (values != null && !values.isEmpty()) {
                    result.put(headerName, String.join(",", values));
                }
            }
        }

        return result;
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
            for (String binarySubtype : HttpConstant.BINARY_DOCUMENT_SUBTYPES) {
                if (lowerContentType.contains(binarySubtype)) {
                    return true;
                }
            }
        }

        // 其他情况默认为非二进制（包括text/*、application/json、application/xml、表单等）
        return false;
    }

    /**
     * 格式化为单行，去除换行符
     *
     * @param content 原始内容
     * @return 单行内容
     */
    public static String formatToSingleLine(String content) {
        if (StrUtil.isBlank(content)) {
            return content;
        }
        // 将所有换行符、制表符替换为空格，并压缩多余空格
        return content.replaceAll("[\r\n\t]+", " ").replaceAll(" +", " ").trim();
    }

}
