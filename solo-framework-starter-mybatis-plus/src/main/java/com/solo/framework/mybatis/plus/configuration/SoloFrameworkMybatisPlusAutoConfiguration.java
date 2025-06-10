package com.solo.framework.mybatis.plus.configuration;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@ConditionalOnClass(MybatisPlusInterceptor.class)
@AutoConfigureBefore(MybatisPlusAutoConfiguration.class)
@EnableConfigurationProperties({ MybatisPlusProperties.class })
public class SoloFrameworkMybatisPlusAutoConfiguration {

    /**
     * 默认数据源配置（HikariCP）
     */
    @Bean
    @ConditionalOnMissingBean(DataSource.class)
    public DataSource dataSource(DataSourceProperties properties) {
        HikariDataSource dataSource = properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();

        // 设置默认连接池参数
        dataSource.setMaximumPoolSize(20);
        dataSource.setMinimumIdle(5);
        dataSource.setIdleTimeout(30000);
        dataSource.setConnectionTimeout(30000);
        dataSource.setMaxLifetime(180000);
        return dataSource;
    }

    /**
     * 默认分页插件
     */
    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    /**
     * 默认全局配置（包含逻辑删除配置）
     */
    @Bean
    @ConditionalOnMissingBean
    public GlobalConfig globalConfig() {
        GlobalConfig.DbConfig dbConfig = new GlobalConfig.DbConfig();
        dbConfig.setIdType(com.baomidou.mybatisplus.annotation.IdType.AUTO);
        dbConfig.setLogicDeleteField("deleted");
        dbConfig.setLogicDeleteValue("1");
        dbConfig.setLogicNotDeleteValue("0");
        dbConfig.setTableUnderline(true);

        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setDbConfig(dbConfig);
        globalConfig.setBanner(false);
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
            configuration.setDefaultExecutorType(org.apache.ibatis.session.ExecutorType.SIMPLE);
            configuration.setLocalCacheScope(org.apache.ibatis.session.LocalCacheScope.STATEMENT);
            configuration.setLogImpl(StdOutImpl.class);
            configuration.setLazyLoadingEnabled(true);
            configuration.setAggressiveLazyLoading(false);
            configuration.setJdbcTypeForNull(null);
        };
    }

}
