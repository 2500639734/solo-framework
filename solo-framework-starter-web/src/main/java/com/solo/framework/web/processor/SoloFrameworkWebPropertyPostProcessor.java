package com.solo.framework.web.processor;

import cn.hutool.core.map.MapUtil;
import com.solo.framework.core.constant.SoloFrameworkWebSwaggerPropertiesConstant;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.Map;

public class SoloFrameworkWebPropertyPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        settingSwaggerEnabled(environment);
    }

    /**
     * 设置 swagger 以及 knife4j 是否启用:
     * 以框架内的 swagger 是否启用 {@link com.solo.framework.core.constant.SoloFrameworkWebSwaggerPropertiesConstant#ENABLED_KEY} 配置为准
     * @param environment 环境对象
     */
    private void settingSwaggerEnabled(ConfigurableEnvironment environment) {
        String swaggerEnabled = environment.getProperty(SoloFrameworkWebSwaggerPropertiesConstant.ENABLED_KEY, String.valueOf(SoloFrameworkWebSwaggerPropertiesConstant.ENABLED_DEFAULT_VALUE));

        Map<String, Object> autoConfigMap = MapUtil.newHashMap();
        autoConfigMap.put(SoloFrameworkWebSwaggerPropertiesConstant.SPRINGFOX_DOCUMENTATION_ENABLED, swaggerEnabled);
        autoConfigMap.put(SoloFrameworkWebSwaggerPropertiesConstant.KEN4J_ENABLED, swaggerEnabled);

        MapPropertySource swaggerProperties = new MapPropertySource(SoloFrameworkWebSwaggerPropertiesConstant.MAP_PROPERTY_SOURCE_KEY, autoConfigMap);
        environment.getPropertySources().addFirst(swaggerProperties);
    }

}
