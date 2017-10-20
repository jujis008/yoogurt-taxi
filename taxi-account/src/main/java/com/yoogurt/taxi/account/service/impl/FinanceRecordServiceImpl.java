package com.yoogurt.taxi.account.service.impl;

import com.yoogurt.taxi.account.dao.FinanceRecordDao;
import com.yoogurt.taxi.account.service.FinanceRecordService;
import com.yoogurt.taxi.dal.beans.FinanceRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Slf4j
@Service
public class FinanceRecordServiceImpl implements FinanceRecordService {
    @Autowired
    private FinanceRecordDao financeRecordDao;
    @Override
    public FinanceRecord save(FinanceRecord record) {
        if (record.getId() == null) {
            financeRecordDao.insert(record);
        }
        financeRecordDao.updateById(record);
        return record;
    }

    @Override
    public int remove(Long id) {
        FinanceRecord financeRecord = get(id);
        if (financeRecord == null) {
            return 0;
        }
        financeRecord.setIsDeleted(Boolean.TRUE);
        return financeRecordDao.updateById(financeRecord);
    }

    @Override
    public int delete(Long id) {
        return financeRecordDao.deleteById(id);
    }

    @Override
    public FinanceRecord get(Long id) {
        return financeRecordDao.selectById(id);
    }

    @Override
    public List<FinanceRecord> getBillRecord(Long userId, Long billId, Long billNo) {
        Example example = new Example(FinanceRecord.class);
        example.setOrderByClause(" gmt_create desc");
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("isDeleted",Boolean.FALSE);
        if (billId != null) {
            criteria.andEqualTo("billId",billId);
        }
        if (billNo != null) {
            criteria.andEqualTo("billNo",billNo);
        }
        return financeRecordDao.selectByExample(example);
    }
}
