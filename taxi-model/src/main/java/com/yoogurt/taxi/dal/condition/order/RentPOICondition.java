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

    private Double lng;

    private Double lat;

    private Integer distance;
}
