package com.yoogurt.taxi.order.service.impl;

import com.yoogurt.taxi.dal.beans.OrderCancelRule;
import com.yoogurt.taxi.order.dao.CancelRuleDao;
import com.yoogurt.taxi.order.service.CancelRuleService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CancelRuleServiceImpl implements CancelRuleService {

    @Autowired
    private CancelRuleDao ruleDao;

    @Override
    public String getIntroduction() {
        List<OrderCancelRule> ruleList = getRules();
        if(CollectionUtils.isEmpty(ruleList)) return StringUtils.EMPTY;
        StringBuilder introduction = new StringBuilder("\n取消订单规则说明：\n");
        ruleList.forEach((OrderCancelRule rule) -> {
            String unit = getCNTimeUnit(rule.getUnit());
            introduction.append("距离交车时间");
            if (rule.getStart() == null || rule.getStart() <= 0) {
                introduction.append(rule.getEnd()).append(unit).append("以下，");
            } else if (rule.getEnd() == null || rule.getEnd() <= 0) {
                introduction.append(rule.getStart()).append(unit).append("以上，");
            } else {
                introduction.append(rule.getEnd()).append(unit).append("以下，");
                introduction.append(rule.getStart()).append(unit).append("以上，");
            }
            if (!rule.getAllowCancel()) {
                introduction.append("不予取消。\n");
            } else if (rule.getDisobey()) {
                BigDecimal percent = rule.getPercent();
                introduction.append("扣除订单金额的").append(percent.multiply(new BigDecimal(100)).intValue()).append("%").append("作为违约金。\n");
            } else {
                introduction.append("无需扣除违约金。\n");
            }
        });
        return introduction.append("违约金将从您的钱包余额中扣除。\n").toString();
    }

    /**
     * 获取匹配的取消违约规则
     *
     * @param time 距离交车时间
     * @param unit 时间单位
     */
    @Override
    public OrderCancelRule getRuleInfo(int time, String unit) {
        if(time <= 0 || StringUtils.isBlank(unit)) return null;
        List<OrderCancelRule> rules = getRules();
        if(CollectionUtils.isEmpty(rules)) return null;
        for (OrderCancelRule rule : rules) {
            //时间单位和时段区间要吻合
            if (unit.equalsIgnoreCase(rule.getUnit()) && time >= rule.getStart()) {
                //正好在某个时段区间内
                if(time <= rule.getEnd()) return rule;
                //xx小时以上的情况，满足如下判定条件
                if(rule.getEnd() == null || rule.getEnd() <= 0) return rule;
            }
        }
        return null;
    }

    @Override
    public List<OrderCancelRule> getRules() {
        Example ex = new Example(OrderCancelRule.class);
        ex.createCriteria().andEqualTo("isDeleted", Boolean.FALSE);
        ex.setOrderByClause("start ASC"); //按照时段下限由小到大排序
        return ruleDao.selectByExample(ex);
    }

    /**
     * 根据英文的时间单位，转换成中文形式的时间单位.
     * @param unit 英文的时间单位
     * @return 中文形式的时间单位
     */
    private String getCNTimeUnit(String unit) {
        if ("MINUTES".equalsIgnoreCase(unit)) {
            return "分钟";
        } else {
            return "小时";
        }
    }
}
