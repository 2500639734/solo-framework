package com.solo.framework.mts.dto.request;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 通用查询请求参数
 * <p>
 * 1. 支持自定义查询字段列表
 * 2. 支持自定义排序字段列表及排序方式
 * 3. 提供方法转换为 MyBatis-Plus 查询条件
 * </p>
 */
@Data
@ApiModel(value = "QueryRequest", description = "通用查询请求参数，支持自定义查询字段、排序字段和排序方式")
public class QueryRequest {

    /**
     * 查询字段列表
     * <p>
     * 可指定需要查询的数据库字段，支持驼峰或下划线写法
     * 例如 userId 或 user_id
     * </p>
     */
    @ApiModelProperty(
            value = "需要查询的字段列表，支持驼峰或下划线，例如 userId 或 user_id。为空表示查询全部字段",
            example = "[\"userId\",\"userName\"]"
    )
    private List<String> fields;

    /**
     * 排序字段列表
     * <p>
     * 可指定排序字段，支持驼峰或下划线写法
     * 例如 userId 或 user_id
     * </p>
     */
    @ApiModelProperty(
            value = "排序字段列表，支持驼峰或下划线，例如 createTime 或 create_time。为空不会排序",
            example = "[\"createTime\"]"
    )
    private List<String> sortFields;

    /**
     * 排序方式
     * <p>
     * ASC-升序, DESC-降序
     * 默认DESC
     * </p>
     */
    @ApiModelProperty(
            value = "排序方式，DESC-降序，ASC-升序，默认DESC",
            example = "DESC"
    )
    private String sortOrder = "DESC";

    /**
     * 根据字段列表生成 select 字段（MyBatis-Plus QueryWrapper 可用）
     *
     * @param clazz 实体类
     * @param wrapper QueryWrapper
     */
    public <T> void applySelectFields(Class<T> clazz, QueryWrapper<T> wrapper) {
        if (CollUtil.isEmpty(this.fields)) {
            return;
        }

        Set<String> entityFields = Arrays.stream(clazz.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toSet());

        List<String> selectColumns = this.fields.stream()
                .map(StrUtil::toUnderlineCase)
                .filter(f -> entityFields.contains(StrUtil.toCamelCase(f)))
                .collect(Collectors.toList());

        if (CollUtil.isNotEmpty(selectColumns)) {
            wrapper.select(selectColumns.toArray(new String[0]));
        }
    }

    /**
     * 根据排序字段列表生成 orderBy
     *
     * @param clazz 实体类
     * @param wrapper QueryWrapper
     */
    public <T> void applyOrderBy(Class<T> clazz, QueryWrapper<T> wrapper) {
        if (CollUtil.isEmpty(this.sortFields)) {
            return;
        }

        Set<String> entityFields = Arrays.stream(clazz.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toSet());

        for (String sortField : this.sortFields) {
            String dbField = StrUtil.toUnderlineCase(sortField);
            if (entityFields.contains(StrUtil.toCamelCase(dbField))) {
                if ("DESC".equalsIgnoreCase(this.sortOrder)) {
                    wrapper.orderByDesc(dbField);
                } else {
                    wrapper.orderByAsc(dbField);
                }
            }
        }
    }

    /**
     * 构建 QueryWrapper，包含 select 与排序
     */
    public <T> QueryWrapper<T> buildQueryWrapper(Class<T> clazz) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        applySelectFields(clazz, wrapper);
        applyOrderBy(clazz, wrapper);
        return wrapper;
    }

}
