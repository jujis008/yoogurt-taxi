package com.yoogurt.taxi.common.condition;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * Description:
 * 支持关键字检索的基本查询条件，不可直接使用。
 * 所有查询条件Dto都要直接或者间接的继承 BaseCondition。
 * 基于文本的单条件查询，统一用keywords接收。
 * @author Eric Lau
 * @Date 2017/8/2.
 */
@Setter
@Getter
public abstract class BaseCondition {

    /**
     * 搜索关键字
     */
    private String keywords;

    public BaseCondition() {
    }

    public BaseCondition(String keywords) {
        this.keywords = keywords;
    }

    /**
     * 对查询条件进行必要的逻辑验证。
     * 简单的验证可以加注解，复杂的验证交给validate()
     * @return true 验证通过，false 验证不通过
     */
    public abstract boolean validate();

    /**
     * 根据关键字构造模糊查询条件，默认是全模糊匹配。
     * @return 在keywords两侧加上%
     */
    public String likes() {
        if(StringUtils.isBlank(this.keywords)) return StringUtils.EMPTY;
        return "%" + this.keywords + "%";
    }

    /**
     * 根据关键字构造模糊查询条件
     * @param matchHead true 表示用前一部分匹配，即在keywords右侧追加%，否则在左侧追加。
     * @return 
     */
    public String likes(boolean matchHead) {
        if(StringUtils.isBlank(this.keywords)) return StringUtils.EMPTY;
        return matchHead ? (this.keywords + "%") : ("%" + this.keywords);
    }
}
