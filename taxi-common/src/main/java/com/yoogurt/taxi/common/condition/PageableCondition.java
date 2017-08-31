package com.yoogurt.taxi.common.condition;

import lombok.Getter;
import lombok.Setter;

/**
 * Description:
 * 可分页的查询条件。
 * @author Eric Lau
 * @Date 2017/8/2.
 */
@Getter
@Setter
public class PageableCondition extends BaseCondition {

    /**
     * 页码，从1开始
     */
    private Integer pageNum = 1;

    /**
     * 一页数据容量，默认15条
     */
    private Integer pageSize = 15;

    /**
     * 跳转至指定页码
     */
    private Integer skip = 1;

    public PageableCondition() {
    }

    public PageableCondition(Integer pageNum, Integer pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public PageableCondition(Integer pageNum, Integer pageSize, Integer skip) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.skip = skip;
    }

    /**
     * 对查询条件进行必要的逻辑验证。
     * 简单的验证可以加注解，复杂的验证交给validate()
     * @return true 验证通过，false 验证不通过
     */
    @Override
    public boolean validate() {

        return pageNum != null && pageNum > 0 && pageSize != null && pageSize > 0 && skip != null && skip > 0;
    }
}
