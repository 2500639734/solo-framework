package com.solo.framework.web.configuration.web.internation;

import com.solo.framework.core.constant.SoloFrameworkPropertiesPrefixConstant;
import com.solo.framework.core.properties.web.internation.SoloFrameworkWebInternationProperties;
import com.solo.framework.web.controller.SoloFrameworkDefaultController;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
@ConditionalOnProperty(name = SoloFrameworkPropertiesPrefixConstant.SOLO_FRAMEWORK_INTERNATION_PREFIX + ".enabled", havingValue = "true", matchIfMissing = true)
@AutoConfigureBefore(WebMvcAutoConfiguration.class)
@EnableConfigurationProperties(SoloFrameworkWebInternationProperties.class)
public class SoloFrameworkWebInternationAutoConfiguration {

    @Setter(onMethod_ = {@Autowired})
    private SoloFrameworkWebInternationProperties soloFrameworkWebInternationProperties;

    @Bean
    @ConditionalOnMissingBean(LocaleResolver.class)
    public LocaleResolver localeResolver() {
        if (SoloFrameworkWebInternationProperties.LocaleResolver.COOKIE.equals(soloFrameworkWebInternationProperties.getLocaleResolver())) {
            CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
            cookieLocaleResolver.setDefaultLocale(soloFrameworkWebInternationProperties.getLocale());
            return cookieLocaleResolver;
        }

        SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
        sessionLocaleResolver.setDefaultLocale(soloFrameworkWebInternationProperties.getLocale());
        return sessionLocaleResolver;
    }

    @Bean
    public SoloFrameworkDefaultController soloFrameworkPathController() {
        return new SoloFrameworkDefaultController();
    }

}
