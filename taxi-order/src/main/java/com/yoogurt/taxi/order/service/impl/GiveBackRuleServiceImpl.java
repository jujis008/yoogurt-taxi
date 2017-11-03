package com.yoogurt.taxi.order.service.impl;

import com.yoogurt.taxi.common.bo.Money;
import com.yoogurt.taxi.dal.beans.OrderGiveBackRule;
import com.yoogurt.taxi.order.dao.GiveBackRuleDao;
import com.yoogurt.taxi.order.service.GiveBackRuleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Service
public class GiveBackRuleServiceImpl implements GiveBackRuleService {

    @Autowired
    private GiveBackRuleDao giveBackRuleDao;

    @Override
    public String getIntroduction() {
        OrderGiveBackRule rule = getRuleInfo();
        if (rule == null) return StringUtils.EMPTY;
        String unit = rule.getUnit();
        if ("HOURS".equalsIgnoreCase(unit)) {
            unit = "小时";
        } else {
            unit = "分钟";
        }

        return "每超出还车时间" + rule.getTime() + unit + "，需缴纳" + rule.getPrice() + "元违约金。";
    }

    @Override
    public OrderGiveBackRule getRuleInfo(long milliseconds) {
        if (milliseconds <= 0) return null;
        OrderGiveBackRule rule = getRuleInfo();
        if (rule == null) return null;
        TimeUnit unit = TimeUnit.valueOf(rule.getUnit());
        long period = unit.toMillis(rule.getTime());
        //没有超出设置好的违约时限，都视为没有违约
        if (period > milliseconds) return null;
        return rule;
    }

    @Override
    public OrderGiveBackRule getRuleInfo() {
        OrderGiveBackRule rule = new OrderGiveBackRule();
        rule.setIsDeleted(Boolean.FALSE);
        //一个时刻只会允许一条还车违约规则生效
        return giveBackRuleDao.selectOne(rule);
    }

    /**
     * 计算罚款金额。
     *
     * @param rule        违约对应的规则
     * @param minutes     超出交车的时长
     * @param limitAmount 最高处罚金额，传入null表示不限
     */
    @Override
    public Money calculate(OrderGiveBackRule rule, int minutes, BigDecimal limitAmount) {
        if (rule == null) return null;
        Money money = new Money(rule.getPrice());
        money.multiplyBy(Math.floor((double) minutes / rule.getTime()));
        Money limitMoney = new Money(limitAmount);
        return limitMoney.compareTo(money) < 0 ? limitMoney : money;
    }
}
