package com.yoogurt.taxi.system.dao.impl;

import com.yoogurt.taxi.common.dao.impl.BaseDao;
import com.yoogurt.taxi.dal.beans.FeedbackType;
import com.yoogurt.taxi.dal.mapper.FeedbackTypeMapper;
import com.yoogurt.taxi.system.dao.FeedbackTypeDao;
import org.springframework.stereotype.Repository;

@Repository
public class FeedbackTypeDaoImpl extends BaseDao<FeedbackTypeMapper,FeedbackType> implements FeedbackTypeDao {
}
