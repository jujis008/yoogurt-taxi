package com.yoogurt.taxi.system.dao.impl;

import com.yoogurt.taxi.common.dao.impl.BaseDao;
import com.yoogurt.taxi.dal.beans.FeedbackRecord;
import com.yoogurt.taxi.dal.mapper.FeedbackRecordMapper;
import com.yoogurt.taxi.system.dao.FeedbackRecordDao;
import org.springframework.stereotype.Repository;

@Repository
public class FeedbackRecordDaoImpl extends BaseDao<FeedbackRecordMapper, FeedbackRecord> implements FeedbackRecordDao {
}
