package com.yoogurt.taxi.order.service.impl;

import com.yoogurt.taxi.dal.beans.OrderGiveBackRule;
import com.yoogurt.taxi.order.dao.GiveBackRuleDao;
import com.yoogurt.taxi.order.service.GiveBackRuleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GiveBackRuleServiceImpl implements GiveBackRuleService {

    @Autowired
    private GiveBackRuleDao giveBackRuleDao;

    @Override
    public String getIntroduction() {
        OrderGiveBackRule rule = getRuleInfo();
        if(rule == null) return StringUtils.EMPTY;
        String unit = rule.getUnit();
        if ("HOURS".equalsIgnoreCase(unit)) {
            unit = "小时";
        } else {
            unit = "分钟";
        }

        return "每超出还车时间" + rule.getTime() + unit + "，需缴纳" + rule.getPrice() + "元违约金。";
    }

    @Override
    public OrderGiveBackRule getRuleInfo(int time, String unit) {
        if (StringUtils.isBlank(unit)) {
            //交车的时间单位默认为 分钟
            unit = "MINUTES";
        }
        OrderGiveBackRule rule = getRuleInfo();
        if(rule == null) return null;
        int period = rule.getTime();
        //时间单位不一样，或者没有超出设置好的违约时限，都视为没有违约
        if (!unit.equalsIgnoreCase(rule.getUnit()) || period > time) return null;
        return rule;
    }

    @Override
    public OrderGiveBackRule getRuleInfo() {
        OrderGiveBackRule rule = new OrderGiveBackRule();
        rule.setIsDeleted(Boolean.FALSE);
        //一个时刻只会允许一条还车违约规则生效
        return giveBackRuleDao.selectOne(rule);
    }
}
