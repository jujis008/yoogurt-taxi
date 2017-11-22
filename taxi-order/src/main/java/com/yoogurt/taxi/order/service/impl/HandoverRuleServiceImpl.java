package com.yoogurt.taxi.order.service.impl;

import com.yoogurt.taxi.common.bo.Money;
import com.yoogurt.taxi.dal.beans.OrderHandoverRule;
import com.yoogurt.taxi.order.dao.HandoverRuleDao;
import com.yoogurt.taxi.order.service.HandoverRuleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * 交车违约规则
 */
@Service
public class HandoverRuleServiceImpl implements HandoverRuleService {

    @Autowired
    private HandoverRuleDao handoverRuleDao;

    /**
     * 获取交车违约规则介绍，根据数据库中的数据动态拼接。
     */
    @Override
    public String getIntroduction() {
        //一个时刻只会允许一条交车违约规则生效
        OrderHandoverRule rule = getRuleInfo();

        //没有设置规则
        if (rule == null) {
            return StringUtils.EMPTY;
        }
        String unit = rule.getUnit();
        if ("HOURS".equalsIgnoreCase(unit)) {
            unit = "小时";
        } else {
            unit = "分钟";
        }

        return "每超出交车时间" + rule.getTime() + unit + "，需缴纳" + rule.getPrice() + "元违约金。";
    }

    /**
     * 根据交车超时时间，获取违约规则，返回null表示没有违约
     *
     * @param milliseconds 交车超出的时间
     */
    @Override
    public OrderHandoverRule getRuleInfo(long milliseconds) {
        if (milliseconds <= 0) {
            return null;
        }
        OrderHandoverRule rule = getRuleInfo();
        if (rule == null) {
            return null;
        }
        TimeUnit unit = TimeUnit.valueOf(rule.getUnit());
        long period = unit.toMillis(rule.getTime());
        //没有超出设置好的违约时限，都视为没有违约
        if (period > milliseconds) {
            return null;
        }
        return rule;
    }

    /**
     * 获取一条交车违约规则。
     * 一个时刻只会允许一条交车违约规则生效。
     */
    @Override
    public OrderHandoverRule getRuleInfo() {
        OrderHandoverRule probe = new OrderHandoverRule();
        probe.setIsDeleted(Boolean.FALSE);
        //一个时刻只会允许一条交车违约规则生效
        return handoverRuleDao.selectOne(probe);
    }

    /**
     * 计算罚款金额。
     *
     * @param rule        违约对应的规则
     * @param time        超出交车的时长
     * @param limitAmount 最高处罚金额，传入null表示不限
     */
    @Override
    public Money calculate(OrderHandoverRule rule, int time, BigDecimal limitAmount) {
        if (rule == null) {
            return null;
        }
        Money limitMoney = new Money(limitAmount);
        Money money = new Money(rule.getPrice());
        money.multiplyBy(Math.floor((double) time / rule.getTime()));
        return limitMoney.compareTo(money) < 0 ? limitMoney : money;
    }


}
