package com.yoogurt.taxi.system.service.impl;

import com.yoogurt.taxi.dal.beans.FeedbackType;
import com.yoogurt.taxi.system.dao.FeedbackTypeDao;
import com.yoogurt.taxi.system.service.FeedbackTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
@Service
public class FeedbackTypeServiceImpl implements FeedbackTypeService {
    @Autowired
    private FeedbackTypeDao feedbackTypeDao;
    @Override
    public List<FeedbackType> getList() {
        Example example = new Example(FeedbackType.class);
        example.createCriteria().andEqualTo("isDeleted", Boolean.FALSE);
        return feedbackTypeDao.selectByExample(example);
    }

    @Override
    public int insert(FeedbackType object) {
        return feedbackTypeDao.insert(object);
    }

    @Override
    public FeedbackType getOne(Long id) {
        return feedbackTypeDao.selectById(id);
    }
}
