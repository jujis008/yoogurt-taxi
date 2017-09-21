package com.yoogurt.taxi.common.condition;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Description:
 * 支持时间段查询
 * @author Eric Lau
 * @Date 2017/8/2.
 */
@Getter
@Setter
public class PeriodCondition extends BaseCondition {

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    public PeriodCondition() {
    }

    public PeriodCondition(Date startTime, Date endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * 对查询条件进行必要的逻辑验证。
     * 简单的验证可以加注解，复杂的验证交给validate()
     *
     * @return true 验证通过，false 验证不通过
     */
    @Override
    public boolean validate() {
        return startTime == null || endTime == null || startTime.compareTo(endTime) <= 0;
    }
}
