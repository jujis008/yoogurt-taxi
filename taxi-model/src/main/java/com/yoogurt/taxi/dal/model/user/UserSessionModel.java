package com.yoogurt.taxi.dal.model.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserSessionModel {
    /**
     * 账户
     */
    private String username;
    /**
     * 用户状态
     */
    private Integer status;

    private Boolean driverAuthenticated;

    private Boolean carAuthenticated;
    /**
     * 姓名
     */
    private String name;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 订单数
     */
    private Integer orderCount =0;

    /**
     * 评分数
     */
    private BigDecimal score = new BigDecimal(0);

    /**
     * 违章数量
     */
    private Integer trafficViolationCount = 0;

    /**
     * 违约数量
     */
    private Integer disobeyCount = 0;
}
