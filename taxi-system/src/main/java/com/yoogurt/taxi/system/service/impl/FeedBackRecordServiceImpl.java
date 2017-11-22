package com.yoogurt.taxi.system.service.impl;

import com.yoogurt.taxi.common.pager.BasePager;
import com.yoogurt.taxi.dal.beans.FeedbackRecord;
import com.yoogurt.taxi.dal.condition.system.FeedbackRecordCondition;
import com.yoogurt.taxi.system.dao.FeedbackRecordDao;
import com.yoogurt.taxi.system.service.FeedBackRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeedBackRecordServiceImpl implements FeedBackRecordService {
    @Autowired
    private FeedbackRecordDao feedbackRecordDao;
    @Override
    public int insert(FeedbackRecord record) {
        return feedbackRecordDao.insert(record);
    }

    @Override
    public BasePager<FeedbackRecord> getWebList(FeedbackRecordCondition condition) {
        return feedbackRecordDao.getWebList(condition);
    }

    @Override
    public FeedbackRecord getOne(Long id) {
        return feedbackRecordDao.selectById(id);
    }

    @Override
    public int update(FeedbackRecord record) {
        return feedbackRecordDao.updateByIdSelective(record);
    }
}
