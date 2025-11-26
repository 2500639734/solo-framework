package com.solo.framework.mts.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.JdbcType;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class TenantEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 企业ID（租户ID）
     */
    @TableField(value = "ent_id", jdbcType = JdbcType.BIGINT, fill = FieldFill.INSERT)
    private Long entId;

}
