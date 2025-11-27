package com.solo.framework.mts.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 通用分页查询请求参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "PageQueryRequest", description = "通用分页查询请求参数，包含分页信息及可选查询/排序字段")
public class PageQueryRequest extends QueryRequest {

    /**
     * 页码
     * <p>
     * 表示当前请求的页码，从1开始计数。
     * 默认值为1，最小值为1
     * </p>
     */
    @ApiModelProperty(value = "当前页码，从1开始计数，最小值1，默认1", example = "1")
    @Min(1)
    private Integer pageNum = 1;

    /**
     * 每页记录数
     * <p>
     * 表示当前页请求的数据条数。
     * 默认值为10，最大值限制为2000
     * </p>
     */
    @ApiModelProperty(value = "每页记录数，最小1，最大2000，默认10", example = "10")
    @Min(1)
    @Max(2000)
    private Integer pageSize = 10;

    /**
     * 获取实际MySQL查询的起始行偏移量
     * <p>
     * MySQL分页查询使用 offset 和 limit 参数
     * offset = (pageNum - 1) * pageSize
     * </p>
     *
     * @return 查询起始行偏移量
     */
    @ApiModelProperty(hidden = true)
    public int getOffset() {
        return (this.pageNum - 1) * this.pageSize;
    }

    /**
     * 获取分页大小
     *
     * @return 每页条数
     */
    @ApiModelProperty(hidden = true)
    public int getLimit() {
        return this.pageSize;
    }

}
