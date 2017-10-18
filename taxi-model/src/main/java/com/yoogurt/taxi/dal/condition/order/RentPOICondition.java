package com.yoogurt.taxi.dal.condition.order;

import com.yoogurt.taxi.common.condition.PeriodCondition;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * 用于客户端地图POI检索的条件
 */
@Getter
@Setter
public class RentPOICondition extends PeriodCondition {

    /**
     * 指定发布人的ID
     */
    private Long userId;

    /**
     * 用户类型：
     * 代理司机类型意味着只能看代理司机发布的求租信息；
     * 正式司机类型意味着只能看正式司机发布的出租信息。
     */
    private Integer userType;

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
     * 指定范围的最小纬度
     */
    private Double minLat;

    /**
     * 要匹配的租单状态。
     * @see com.yoogurt.taxi.dal.enums.OrderStatus
     */
    private Integer status;

    /**
     * 距离，默认1km。
     * 此字段已废弃
     */
    private Integer distance;

    public RentPOICondition() {
    }

    public RentPOICondition(Long userId, Double maxLng, Double minLng, Double maxLat, Double minLat, Integer status, Integer distance) {
        this.userId = userId;
        this.maxLng = maxLng;
        this.minLng = minLng;
        this.maxLat = maxLat;
        this.minLat = minLat;
        this.status = status;
        this.distance = distance;
    }

    public RentPOICondition(Date startTime, Date endTime) {
        super(startTime, endTime);
    }

    /**
     * 对查询条件进行必要的逻辑验证。
     * 简单的验证可以加注解，复杂的验证交给validate()
     *
     * @return true 验证通过，false 验证不通过
     */
    @Override
    public boolean validate() {
        return super.validate() && (userId == null || userId > 0)
                && (minLng == null || maxLng == null || minLng <= maxLng)
                && (minLat == null || maxLat == null || minLat <= maxLat);
    }
}
