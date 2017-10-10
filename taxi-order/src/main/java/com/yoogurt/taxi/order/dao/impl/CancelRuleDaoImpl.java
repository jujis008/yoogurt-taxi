package com.yoogurt.taxi.order.dao.impl;

import com.yoogurt.taxi.common.dao.impl.BaseDao;
import com.yoogurt.taxi.dal.beans.OrderCancelRule;
import com.yoogurt.taxi.dal.mapper.OrderCancelRuleMapper;
import com.yoogurt.taxi.order.dao.CancelRuleDao;
import org.springframework.stereotype.Repository;

@Repository
public class CancelRuleDaoImpl extends BaseDao<OrderCancelRuleMapper, OrderCancelRule> implements CancelRuleDao {
}
