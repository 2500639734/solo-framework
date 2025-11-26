package com.solo.framework.mts.properties;

import com.baomidou.mybatisplus.annotation.IdType;
import lombok.Data;

/**
 * MyBatis Plus 全局配置
 */
@Data
public class SoloFrameworkMtsGlobalConfigProperties {

    /**
     * 主键类型
     */
    private IdType idType = IdType.AUTO;

    /**
     * 逻辑删除字段名
     */
    private String logicDeleteField = "deleted";

    /**
     * 逻辑删除值（已删除）
     */
    private String logicDeleteValue = "1";

    /**
     * 逻辑未删除值
     */
    private String logicNotDeleteValue = "0";

    /**
     * 表名是否使用下划线命名
     */
    private Boolean tableUnderline = true;

    /**
     * 是否显示 MyBatis Plus Banner
     */
    private Boolean banner = false;

}
