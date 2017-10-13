package com.yoogurt.taxi.account.service.impl;

import com.yoogurt.taxi.account.dao.FinanceBankCardDao;
import com.yoogurt.taxi.account.service.FinanceBankCardService;
import com.yoogurt.taxi.dal.beans.FinanceBankCard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FinanceBankCardServiceImpl implements FinanceBankCardService{
    @Autowired
    private FinanceBankCardDao financeBankCardDao;
    @Override
    public int save(FinanceBankCard bankCard) {
        if (bankCard == null) {
            return financeBankCardDao.insert(bankCard);
        }
        return financeBankCardDao.updateById(bankCard);
    }

    @Override
    public int remove(Long id) {
        FinanceBankCard financeBankCard = get(id);
        if (financeBankCard == null) {
            return 0;
        }
        financeBankCard.setIsDeleted(Boolean.TRUE);
        return financeBankCardDao.updateById(financeBankCard);
    }

    @Override
    public FinanceBankCard get(Long id) {
        return financeBankCardDao.selectById(id);
    }

    @Override
    public List<FinanceBankCard> getList(Long userId) {
        return null;
    }
}
