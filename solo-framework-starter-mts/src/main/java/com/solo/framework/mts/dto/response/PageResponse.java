package com.solo.framework.mts.dto.response;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * 通用分页响应对象
 *
 * 该对象用于封装分页查询接口的返回结果，包含以下信息：
 * 1. 当前页码
 * 2. 每页大小
 * 3. 总记录数
 * 4. 总页数
 * 5. 数据列表
 *
 * @param <T> 数据列表中元素的类型
 */
@Data
@ApiModel(value = "PageResponse", description = "通用分页响应对象，封装分页信息和数据列表")
public class PageResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     * <p>
     * 表示返回数据的当前页编号，从1开始。
     * 例如 current=1 表示第一页，current=2 表示第二页
     */
    @ApiModelProperty(value = "当前页码，从1开始", example = "1", required = true)
    private Long currentPageNum;

    /**
     * 每页记录数
     * <p>
     * 表示每页返回的数据条数，用于分页计算。
     * 例如 size=10 表示每页返回10条记录
     */
    @ApiModelProperty(value = "每页记录数", example = "10", required = true)
    private Long pageSize;

    /**
     * 总记录数
     * <p>
     * 表示满足查询条件的总记录数量，用于计算总页数。
     */
    @ApiModelProperty(value = "总记录数", example = "100", required = true)
    private Long total;

    /**
     * 总页数
     * <p>
     * 根据总记录数和每页大小计算得出总页数。
     * 例如 total=100, size=10，则 pages=10
     */
    @ApiModelProperty(value = "总页数，根据总记录数和每页大小计算", example = "10", required = true)
    private Long totalPages;

    /**
     * 数据列表
     * <p>
     * 分页返回的数据列表，列表元素类型为泛型T。
     * 例如 List<User> records 表示当前页的用户数据
     */
    @ApiModelProperty(value = "分页数据列表，泛型T为列表元素类型", required = true)
    private List<T> records;

    /**
     * 默认构造函数
     */
    public PageResponse() {

    }

    /**
     * 构造函数
     *
     * @param currentPageNum 当前页码
     * @param pageSize 每页记录数
     * @param total   总记录数
     * @param records 当前页数据列表
     */
    public PageResponse(Long currentPageNum, Long pageSize, Long total, List<T> records) {
        this.currentPageNum = currentPageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.records = records;
        this.totalPages = (Objects.nonNull(total) && Objects.nonNull(pageSize) && total > 0 && pageSize > 0)
                ? (total + pageSize - 1) / pageSize
                : 0;
    }

    /**
     * 构造函数：将 MyBatis-Plus 分页对象 Page 转换为 PageResponse
     *
     * @param page MyBatis-Plus 分页对象
     */
    public PageResponse(Page<T> page) {
        if (Objects.nonNull(page)) {
            this.currentPageNum = page.getCurrent();
            this.pageSize = page.getSize();
            this.total = page.getTotal();
            this.records = page.getRecords();
            this.totalPages = page.getPages();
        } else {
            this.currentPageNum = 0L;
            this.pageSize = 0L;
            this.total = 0L;
            this.totalPages = 0L;
            this.records = null;
        }
    }

}
