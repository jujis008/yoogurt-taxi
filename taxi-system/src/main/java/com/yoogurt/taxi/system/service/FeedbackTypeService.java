package com.yoogurt.taxi.system.service;

import com.yoogurt.taxi.dal.beans.FeedbackType;

import java.util.List;

public interface FeedbackTypeService {
    List<FeedbackType> getList();
    int insert(FeedbackType object);
    FeedbackType getOne(Long id);
}
