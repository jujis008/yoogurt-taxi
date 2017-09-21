package com.yoogurt.taxi.common.condition;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Description:
 * 支持分页+时段查询
 * @author Eric Lau
 * @Date 2017/8/2.
 */
@Getter
@Setter
public class PeriodWithPageableCondition extends PageableCondition {

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 对查询条件进行必要的逻辑验证。
     * 简单的验证可以加注解，复杂的验证交给validate()
     *5
     * @return true 验证通过，false 验证不通过
     */
    @Override
    public boolean validate() {

        return startTime == null || endTime == null || startTime.compareTo(endTime) <= 0 && super.validate();
    }
}
