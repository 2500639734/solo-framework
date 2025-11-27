package com.solo.framework.mts.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.JdbcType;

import java.io.Serializable;

@ApiModel(value = "TenantEntity", description = "租户实体类，继承BaseEntity，包含企业ID（租户ID）字段")
@EqualsAndHashCode(callSuper = true)
@Data
public class TenantEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 企业ID（租户ID）
     */
    @ApiModelProperty(value = "企业ID（租户ID），预留字段，暂不支持自动填充", example = "1001")
    @TableField(value = "ent_id", jdbcType = JdbcType.BIGINT, fill = FieldFill.INSERT)
    private Long entId;

}
