package com.solo.framework.mts.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 创建人代码
     */
    @TableField(value = "create_user_code", jdbcType = JdbcType.VARCHAR, fill = FieldFill.INSERT)
    private String createUserCode;

    /**
     * 创建人姓名
     */
    @TableField(value = "create_user_name", jdbcType = JdbcType.VARCHAR, fill = FieldFill.INSERT)
    private String createUserName;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", jdbcType = JdbcType.TIMESTAMP, fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新人代码
     */
    @TableField(value = "update_user_code", jdbcType = JdbcType.VARCHAR, fill = FieldFill.INSERT_UPDATE)
    private String updateUserCode;

    /**
     * 更新人名称
     */
    @TableField(value = "update_user_name", jdbcType = JdbcType.VARCHAR, fill = FieldFill.INSERT_UPDATE)
    private String updateUserName;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", jdbcType = JdbcType.TIMESTAMP, fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 删除标记（0 未删除，1 已删除）
     */
    @TableLogic
    @TableField(value = "deleted", jdbcType = JdbcType.INTEGER, fill = FieldFill.INSERT)
    private Integer deleted;

    /**
     * 备注
     */
    @TableField(value = "remark", jdbcType = JdbcType.VARCHAR)
    private String remark;

    /**
     * 乐观锁版本号
     */
    @Version
    @TableField(value = "version", jdbcType = JdbcType.INTEGER)
    private Integer version;

}
