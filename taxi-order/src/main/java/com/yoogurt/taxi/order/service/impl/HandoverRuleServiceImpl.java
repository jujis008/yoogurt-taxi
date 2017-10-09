package com.yoogurt.taxi.order.service.impl;

import com.yoogurt.taxi.common.bo.Money;
import com.yoogurt.taxi.dal.beans.OrderHandoverRule;
import com.yoogurt.taxi.order.dao.HandoverRuleDao;
import com.yoogurt.taxi.order.service.HandoverRuleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        if(rule == null) return StringUtils.EMPTY;
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
     * @param time 交车超出的时间
     * @param unit 时间单位：MINUTES-分钟，HOURS-小时
     */
    @Override
    public OrderHandoverRule getRuleInfo(int time, String unit) {
        if (StringUtils.isBlank(unit)) {
            //交车的时间单位默认为 分钟
            unit = "MINUTES";
        }
        OrderHandoverRule rule = getRuleInfo();
        if(rule == null) return null;
        int period = rule.getTime();
        //时间单位不一样，或者没有超出设置好的违约时限，都视为没有违约
        if (!unit.equalsIgnoreCase(rule.getUnit()) || period > time) return null;
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
     * @param rule 违约对应的规则
     * @param time 超出交车的时长
     */
    @Override
    public Money calculate(OrderHandoverRule rule, int time) {
        if(rule == null) return null;
        Money money = new Money(rule.getPrice());
        money.multiplyBy(Math.floor((double) time / rule.getTime()));
        return money;
    }


}
