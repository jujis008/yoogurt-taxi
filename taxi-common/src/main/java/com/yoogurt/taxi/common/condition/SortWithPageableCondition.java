package com.yoogurt.taxi.common.condition;

import lombok.Getter;
import lombok.Setter;

/**
 * Description:
 * 支持结果集排序和分页的查询条件。
 * @author Eric Lau
 * @Date 2017/09/21.
 */
@Setter
@Getter
public class SortWithPageableCondition extends PageableCondition {

    private String sortName;

    private String sortOrder;

}
