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
}
