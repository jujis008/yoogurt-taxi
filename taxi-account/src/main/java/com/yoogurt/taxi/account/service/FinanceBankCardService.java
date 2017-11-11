package com.yoogurt.taxi.account.service;

import com.yoogurt.taxi.dal.beans.FinanceBankCard;

import java.util.List;

public interface FinanceBankCardService {
    int save(FinanceBankCard bankCard);
    int remove(Long id);
    FinanceBankCard get(Long id);
    List<FinanceBankCard> getList(String userId);
}
