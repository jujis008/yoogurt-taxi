package com.yoogurt.taxi.order.dao.impl;

import com.yoogurt.taxi.common.dao.impl.BaseDao;
import com.yoogurt.taxi.dal.beans.OrderHandoverRule;
import com.yoogurt.taxi.dal.mapper.OrderHandoverRuleMapper;
import com.yoogurt.taxi.order.dao.HandoverRuleDao;
import org.springframework.stereotype.Repository;

@Repository
public class HandoverRuleDaoImpl extends BaseDao<OrderHandoverRuleMapper, OrderHandoverRule> implements HandoverRuleDao {
}
