package com.yoogurt.taxi.system.service;

import com.yoogurt.taxi.common.pager.BasePager;
import com.yoogurt.taxi.dal.beans.FeedbackRecord;
import com.yoogurt.taxi.dal.condition.system.FeedbackRecordCondition;

public interface FeedBackRecordService {
    int insert(FeedbackRecord record);

    BasePager<FeedbackRecord> getWebList(FeedbackRecordCondition condition);

    FeedbackRecord getOne(Long id);

    int update(FeedbackRecord record);
}
