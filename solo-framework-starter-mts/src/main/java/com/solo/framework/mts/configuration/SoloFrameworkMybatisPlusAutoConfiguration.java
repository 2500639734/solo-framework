package com.solo.framework.mts.configuration;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.solo.framework.core.constant.SoloFrameworkPropertiesPrefixConstant;
import com.solo.framework.mts.handler.DefaultMetaObjectHandler;
import com.solo.framework.mts.properties.SoloFrameworkMtsDataSourceProperties;
import com.solo.framework.mts.properties.SoloFrameworkMtsGlobalConfigProperties;
import com.solo.framework.mts.properties.SoloFrameworkMtsProperties;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.LocalCacheScope;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@ConditionalOnClass(MybatisPlusInterceptor.class)
@AutoConfigureBefore(MybatisPlusAutoConfiguration.class)
@EnableConfigurationProperties({ MybatisPlusProperties.class, SoloFrameworkMtsProperties.class })
public class SoloFrameworkMybatisPlusAutoConfiguration {

    /**
     * 默认数据源配置（HikariCP）
     */
    @Bean
    @ConditionalOnMissingBean(DataSource.class)
    public DataSource dataSource(DataSourceProperties properties, SoloFrameworkMtsProperties mtsProperties) {
        SoloFrameworkMtsDataSourceProperties dsProps = mtsProperties.getDatasource();
        
        HikariDataSource dataSource = properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();

        // 使用配置的连接池参数
        dataSource.setMaximumPoolSize(dsProps.getMaximumPoolSize());
        dataSource.setMinimumIdle(dsProps.getMinimumIdle());
        dataSource.setIdleTimeout(dsProps.getIdleTimeout());
        dataSource.setConnectionTimeout(dsProps.getConnectionTimeout());
        dataSource.setMaxLifetime(dsProps.getMaxLifetime());
        return dataSource;
    }

    /**
     * 默认插件配置
     */
    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        // 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    /**
     * 默认全局配置（包含逻辑删除配置）
     */
    @Bean
    @ConditionalOnMissingBean
    public GlobalConfig globalConfig(SoloFrameworkMtsProperties mtsProperties) {
        SoloFrameworkMtsGlobalConfigProperties gcProps = mtsProperties.getGlobalConfig();
        
        GlobalConfig.DbConfig dbConfig = new GlobalConfig.DbConfig();
        // 使用配置的全局参数
        dbConfig.setIdType(gcProps.getIdType());
        dbConfig.setLogicDeleteField(gcProps.getLogicDeleteField());
        dbConfig.setLogicDeleteValue(gcProps.getLogicDeleteValue());
        dbConfig.setLogicNotDeleteValue(gcProps.getLogicNotDeleteValue());
        dbConfig.setTableUnderline(gcProps.getTableUnderline());

        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setDbConfig(dbConfig);
        globalConfig.setBanner(gcProps.getBanner());
        return globalConfig;
    }

    /**
     * MyBatis 配置自定义器（配置SQL日志、驼峰命名、懒加载等）
     */
    @Bean
    @ConditionalOnMissingBean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> {
            configuration.setMapUnderscoreToCamelCase(true);
            configuration.setCacheEnabled(false);
            configuration.setDefaultExecutorType(ExecutorType.SIMPLE);
            configuration.setLocalCacheScope(LocalCacheScope.STATEMENT);
            configuration.setLogImpl(StdOutImpl.class);
            configuration.setLazyLoadingEnabled(true);
            configuration.setAggressiveLazyLoading(false);
            configuration.setJdbcTypeForNull(null);
        };
    }

    /**
     * 默认元数据处理器（自动填充创建时间、更新时间、逻辑删除字段）
     */
    @Bean
    @ConditionalOnMissingBean(MetaObjectHandler.class)
    @ConditionalOnProperty(
            name = SoloFrameworkPropertiesPrefixConstant.SOLO_FRAMEWORK_MTS_PREFIX + ".enableMetaObjectHandler",
            havingValue = "true",
            matchIfMissing = true
    )
    public MetaObjectHandler defaultMetaObjectHandler() {
        return new DefaultMetaObjectHandler();
    }

}
