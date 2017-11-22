package com.yoogurt.taxi.pay.service.impl;

import com.yoogurt.taxi.pay.dao.EventTaskDao;
import com.yoogurt.taxi.pay.doc.EventTask;
import com.yoogurt.taxi.pay.service.EventTaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventTaskServiceImpl implements EventTaskService {

    @Autowired
    private EventTaskDao eventTaskDao;

    @Override
    public EventTask getEventTask(String taskId) {
        if (StringUtils.isBlank(taskId)) {
            return null;
        }
        return eventTaskDao.findOne(taskId);
    }

    @Override
    public EventTask addEventTask(EventTask task) {
        if (task == null) {
            return null;
        }
        return eventTaskDao.insert(task);
    }

    @Override
    public EventTask saveEventTask(EventTask task) {
        if (task == null) {
            return null;
        }
        return eventTaskDao.save(task);
    }

    @Override
    public boolean deleteEventTask(String taskId) {
        if (StringUtils.isBlank(taskId)) {
            return false;
        }
        try {
            eventTaskDao.delete(taskId);
        } catch (Exception e) {
            log.error("删除回调任务信息异常, {}", e);
            return false;
        }
        return true;
    }
}
