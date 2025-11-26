package com.solo.framework.mts.properties;

import lombok.Data;

/**
 * HikariCP 数据源连接池配置
 */
@Data
public class SoloFrameworkMtsDataSourceProperties {

    /**
     * 连接池最大连接数
     */
    private Integer maximumPoolSize = 20;

    /**
     * 连接池最小空闲连接数
     */
    private Integer minimumIdle = 5;

    /**
     * 连接空闲超时时间（毫秒）
     */
    private Long idleTimeout = 30000L;

    /**
     * 连接超时时间（毫秒）
     */
    private Long connectionTimeout = 30000L;

    /**
     * 连接最大生命周期（毫秒）
     */
    private Long maxLifetime = 180000L;

}
