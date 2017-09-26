package com.yoogurt.taxi.dal.condition.order;

import com.yoogurt.taxi.common.condition.PeriodCondition;
import lombok.Getter;
import lombok.Setter;

/**
 * 用于客户端地图POI检索的条件
 */
@Getter
@Setter
public class RentPOICondition extends PeriodCondition {

    /**
     * 指定范围的最大经度
     */
    private Double maxLng;

    /**
     * 指定范围的最小经度
     */
    private Double minLng;

    /**
     * 指定范围的最大纬度
     */
    private Double maxLat;

    /**
     * 指定范围的最大纬度
     */
    private Double minLat;

    /**
     * 距离，默认1km。
     * 此字段已废弃
     */
    private Integer distance;

    /**
     * 对查询条件进行必要的逻辑验证。
     * 简单的验证可以加注解，复杂的验证交给validate()
     *
     * @return true 验证通过，false 验证不通过
     */
    @Override
    public boolean validate() {
        return super.validate()
                && (minLng == null || maxLng == null || minLng <= maxLng)
                && (minLat == null || maxLat == null || minLat <= maxLat);
    }
}
