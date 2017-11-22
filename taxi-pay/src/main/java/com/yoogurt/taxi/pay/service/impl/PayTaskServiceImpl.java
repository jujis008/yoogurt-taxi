package com.yoogurt.taxi.pay.service.impl;

import com.yoogurt.taxi.pay.dao.PayTaskDao;
import com.yoogurt.taxi.pay.doc.PayTask;
import com.yoogurt.taxi.pay.service.PayTaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PayTaskServiceImpl implements PayTaskService {

    @Autowired
    private PayTaskDao payTaskDao;

    @Override
    public PayTask getPayTask(String taskId) {
        if(StringUtils.isBlank(taskId)) {
            return null;
        }
        return payTaskDao.findOne(taskId);
    }

    @Override
    public PayTask addPayTask(PayTask task) {
        if(task == null) {
            return null;
        }
        return payTaskDao.insert(task);
    }

    @Override
    public PayTask savePayTask(PayTask task) {
        if(task == null) {
            return null;
        }
        return payTaskDao.save(task);
    }

    @Override
    public boolean deletePayTask(String taskId) {
        if(StringUtils.isBlank(taskId)) {
            return false;
        }
        try {
            payTaskDao.delete(taskId);
        } catch (Exception e) {
            log.error("删除支付任务信息异常, {}", e);
            return false;
        }
        return true;
    }
}
