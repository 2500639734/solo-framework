package com.solo.framework.mts.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

import java.io.Serializable;
import java.time.LocalDateTime;

@ApiModel(value = "BaseEntity", description = "基础实体类，包含创建人、更新时间、逻辑删除、乐观锁和备注字段")
@Data
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 创建人代码
     */
    @ApiModelProperty(value = "创建人代码", example = "admin")
    @TableField(value = "creator_code", jdbcType = JdbcType.VARCHAR, fill = FieldFill.INSERT)
    private String creatorCode;

    /**
     * 创建人姓名
     */
    @ApiModelProperty(value = "创建人姓名", example = "管理员")
    @TableField(value = "creator_name", jdbcType = JdbcType.VARCHAR, fill = FieldFill.INSERT)
    private String creatorName;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间，自动填充", example = "2025-11-27T12:00:00")
    @TableField(value = "created_at", jdbcType = JdbcType.TIMESTAMP, fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新人代码
     */
    @ApiModelProperty(value = "更新人代码", example = "admin")
    @TableField(value = "modifier_code", jdbcType = JdbcType.VARCHAR, fill = FieldFill.INSERT_UPDATE)
    private String modifierCode;

    /**
     * 更新人姓名
     */
    @ApiModelProperty(value = "更新人姓名", example = "管理员")
    @TableField(value = "modifier_name", jdbcType = JdbcType.VARCHAR, fill = FieldFill.INSERT_UPDATE)
    private String modifierName;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间，自动填充", example = "2025-11-27T12:10:00")
    @TableField(value = "last_modified_at", jdbcType = JdbcType.TIMESTAMP, fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime lastModifiedAt;

    /**
     * 删除标记（0 未删除，1 已删除）
     */
    @ApiModelProperty(value = "删除标记（0 未删除，1 已删除），逻辑删除", example = "0")
    @TableLogic
    @TableField(value = "deleted", jdbcType = JdbcType.INTEGER, fill = FieldFill.INSERT)
    private Integer deleted;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注信息", example = "此记录为测试数据")
    @TableField(value = "remark", jdbcType = JdbcType.VARCHAR)
    private String remark;

    /**
     * 乐观锁版本号
     */
    @ApiModelProperty(value = "乐观锁版本号，更新时自动加1", example = "1")
    @Version
    @TableField(value = "version", jdbcType = JdbcType.INTEGER)
    private Integer version;

}
