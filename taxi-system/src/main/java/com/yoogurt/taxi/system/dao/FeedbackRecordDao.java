package com.yoogurt.taxi.system.dao;

import com.yoogurt.taxi.common.dao.IDao;
import com.yoogurt.taxi.common.pager.Pager;
import com.yoogurt.taxi.dal.beans.FeedbackRecord;
import com.yoogurt.taxi.dal.condition.system.FeedbackRecordCondition;
import com.yoogurt.taxi.dal.mapper.FeedbackRecordMapper;

public interface FeedbackRecordDao extends IDao<FeedbackRecordMapper,FeedbackRecord> {
    Pager<FeedbackRecord> getWebList(FeedbackRecordCondition condition);
}
