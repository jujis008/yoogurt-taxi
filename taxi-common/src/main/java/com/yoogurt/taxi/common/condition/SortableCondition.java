package com.yoogurt.taxi.common.condition;


import lombok.Getter;
import lombok.Setter;

/**
 * Description:
 * 支持结果集排序的查询条件。
 * @author Eric Lau
 * @Date 2017/09/21.
 */
@Setter
@Getter
public abstract class SortableCondition extends BaseCondition {

    private String sortName;

    private String sortOrder;
}
