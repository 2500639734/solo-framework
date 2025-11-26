package com.solo.framework.mts.properties;

import com.solo.framework.core.constant.SoloFrameworkPropertiesPrefixConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * MyBatis Plus 相关配置
 */
@Data
@ConfigurationProperties(prefix = SoloFrameworkPropertiesPrefixConstant.SOLO_FRAMEWORK_MTS_PREFIX)
public class SoloFrameworkMtsProperties {

    /**
     * 数据源连接池配置
     */
    @NestedConfigurationProperty
    private SoloFrameworkMtsDataSourceProperties datasource = new SoloFrameworkMtsDataSourceProperties();

    /**
     * 全局配置
     */
    @NestedConfigurationProperty
    private SoloFrameworkMtsGlobalConfigProperties globalConfig = new SoloFrameworkMtsGlobalConfigProperties();

    /**
     * 是否启用默认元数据处理器（自动填充创建时间、更新时间、逻辑删除字段）
     */
    private Boolean enableMetaObjectHandler = true;

}
