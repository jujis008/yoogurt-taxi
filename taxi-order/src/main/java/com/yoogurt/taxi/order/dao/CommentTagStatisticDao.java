package com.yoogurt.taxi.order.dao;

import com.yoogurt.taxi.common.dao.IDao;
import com.yoogurt.taxi.dal.beans.CommentTagStatistic;
import com.yoogurt.taxi.dal.mapper.CommentTagStatisticMapper;

public interface CommentTagStatisticDao extends IDao<CommentTagStatisticMapper, CommentTagStatistic> {

    int saveStatistic(CommentTagStatistic statistic);
}
