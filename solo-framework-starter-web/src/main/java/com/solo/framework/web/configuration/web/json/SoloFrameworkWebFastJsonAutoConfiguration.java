package com.solo.framework.web.configuration.web.json;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring.http.converter.FastJsonHttpMessageConverter;
import com.solo.framework.core.context.SoloFrameworkContextHolder;
import com.solo.framework.core.properties.web.fastjson.SoloFrameworkWebFastJsonProperties;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import java.nio.charset.Charset;
import java.util.stream.Collectors;

@Configuration
@EnableConfigurationProperties(SoloFrameworkWebFastJsonProperties.class)
public class SoloFrameworkWebFastJsonAutoConfiguration {

    @Setter(onMethod_ = {@Autowired})
    private SoloFrameworkWebFastJsonProperties soloFrameworkWebFastJsonProperties;

    @Bean
    @ConditionalOnMissingBean(FastJsonHttpMessageConverter.class)
    public FastJsonHttpMessageConverter fastJsonHttpMessageConverter() {
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        fastJsonHttpMessageConverter.setFastJsonConfig(ObjectUtil.defaultIfNull(fastJsonConfig(), SoloFrameworkContextHolder.getBean(FastJsonConfig.class)));
        fastJsonHttpMessageConverter.setDefaultCharset(Charset.forName(soloFrameworkWebFastJsonProperties.httpChartSetIfNullDefault()));
        fastJsonHttpMessageConverter.setSupportedMediaTypes(soloFrameworkWebFastJsonProperties.supportedMediaTypesIfNullDefault().stream().map(MediaType::parseMediaType).collect(Collectors.toList()));
        return fastJsonHttpMessageConverter;
    }

    @Bean
    @ConditionalOnMissingBean(FastJsonConfig.class)
    public FastJsonConfig fastJsonConfig() {
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setCharset(Charset.forName(soloFrameworkWebFastJsonProperties.chartSetIfNullDefault()));
        fastJsonConfig.setDateFormat(soloFrameworkWebFastJsonProperties.dateFormatIfNullDefault());
        fastJsonConfig.setReaderFeatures(soloFrameworkWebFastJsonProperties.readerFeaturesIfNullDefault().stream().map(r -> JSONReader.Feature.valueOf(StrUtil.upperFirst(r))).toArray(JSONReader.Feature[]::new));
        fastJsonConfig.setWriterFeatures(soloFrameworkWebFastJsonProperties.writerFeaturesIfNullDefault().stream().map(w -> JSONWriter.Feature.valueOf(StrUtil.upperFirst(w))).toArray(JSONWriter.Feature[]::new));
        return fastJsonConfig;
    }

}





