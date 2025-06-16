package com.solo.framework.core.properties.web.internation;

import cn.hutool.core.collection.CollUtil;
import com.solo.framework.core.constant.SoloFrameworkPropertiesPrefixConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Set;

@Data
@ConfigurationProperties(prefix = SoloFrameworkPropertiesPrefixConstant.SOLO_FRAMEWORK_WEB_INTERNATION_PREFIX)
public class SoloFrameworkWebInternationProperties {

    /**
     * 是否启用国际化
     */
    private boolean enabled = false;

    /**
     * 国际化资源文件目录(resources)
     */
    private Set<String> baseNames = CollUtil.newHashSet("i18n/solo-framework_messages", "i18n/messages", "i18n/validation/messages");

    /**
     * 国际化资源文件编码
     */
    private Charset encoding = StandardCharsets.UTF_8;

    /**
     * 国际化区域解析器
     */
    private LocaleResolver localeResolver = LocaleResolver.SESSION;

    /**
     * 国际化语言环境
     */
    private Locale locale = Locale.CHINA;

    public enum LocaleResolver {

        SESSION,

        COOKIE

    }

}
