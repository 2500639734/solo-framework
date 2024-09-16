package com.solo.framework.core.properties.web.fastjson;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.StrUtil;
import com.solo.framework.core.constant.SoloFrameworkPropertiesPrefixConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.charset.StandardCharsets;
import java.util.Set;


@Data
@ConfigurationProperties(prefix = SoloFrameworkPropertiesPrefixConstant.SOLO_FRAMEWORK_FAST_JSON_PREFIX)
public class SoloFrameworkWebFastJsonProperties {

    /**
     * fast json 编码（Http）
     * {@link java.nio.charset.StandardCharsets}
     */
    private String httpChartSet;

    /**
     * fast json 支持的媒体类型（Http）
     * 全小写 {@link org.springframework.http.MediaType}
     */
    private Set<String> supportedMediaTypes;

    /**
     * fast json 编码（序列化 / 反序列化）
     * {@link java.nio.charset.StandardCharsets}
     */
    private String chartSet;

    /**
     * 日期格式
     * {@link cn.hutool.core.date.DatePattern}
     */
    private String dateFormat;

    /**
     * fast json 反序列化配置
     * 标准驼峰格式(首字母小写) {@link com.alibaba.fastjson2.JSONReader.Feature}
     */
    private Set<String> readerFeatures;

    /**
     * fast json 序列化配置
     * 标准驼峰格式(首字母小写) {@link com.alibaba.fastjson2.JSONWriter.Feature}
     */
    private Set<String> writerFeatures;

    /*
     ********************************* 默认值方法 start ************************************
     *** 没有配置 || 仅配置key, value 为null或''时
     ***    - 使用默认配置
     *** 不以get作为方法前缀, 避免被错误识别为配置文件提示
     */

    public String httpChartSetIfNullDefault() {
        return StrUtil.blankToDefault(httpChartSet, StandardCharsets.UTF_8.name());
    }

    public Set<String> supportedMediaTypesIfNullDefault() {
        return CollUtil.defaultIfEmpty(supportedMediaTypes, CollUtil.newHashSet("application/json"));
    }

    public String chartSetIfNullDefault() {
        return StrUtil.blankToDefault(chartSet, StandardCharsets.UTF_8.name());
    }

    public String dateFormatIfNullDefault() {
        return StrUtil.blankToDefault(dateFormat, DatePattern.NORM_DATETIME_PATTERN);
    }

    public Set<String> readerFeaturesIfNullDefault() {
        return CollUtil.defaultIfEmpty(readerFeatures, CollUtil.newHashSet("fieldBased", "supportArrayToBean"));
    }

    public Set<String> writerFeaturesIfNullDefault() {
        return CollUtil.defaultIfEmpty(writerFeatures, CollUtil.newHashSet("prettyFormat"));
    }

}