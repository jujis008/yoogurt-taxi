package com.yoogurt.taxi.order.dao.impl;

import com.yoogurt.taxi.common.dao.impl.BaseDao;
import com.yoogurt.taxi.dal.beans.CommentTagStatistic;
import com.yoogurt.taxi.dal.mapper.CommentTagStatisticMapper;
import com.yoogurt.taxi.order.dao.CommentTagStatisticDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CommentTagStatisticDaoImpl extends BaseDao<CommentTagStatisticMapper, CommentTagStatistic> implements CommentTagStatisticDao {

    @Autowired
    private CommentTagStatisticMapper mapper;

    @Override
    public int saveStatistic(CommentTagStatistic statistic) {
        return mapper.saveStatistic(statistic);
    }
}
