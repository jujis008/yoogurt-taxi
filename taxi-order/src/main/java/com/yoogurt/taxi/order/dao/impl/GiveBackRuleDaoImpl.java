package com.yoogurt.taxi.order.dao.impl;

import com.yoogurt.taxi.common.dao.impl.BaseDao;
import com.yoogurt.taxi.dal.beans.OrderGiveBackRule;
import com.yoogurt.taxi.dal.mapper.OrderGiveBackRuleMapper;
import com.yoogurt.taxi.order.dao.GiveBackRuleDao;
import org.springframework.stereotype.Repository;

@Repository
public class GiveBackRuleDaoImpl extends BaseDao<OrderGiveBackRuleMapper, OrderGiveBackRule> implements GiveBackRuleDao{
}
