package com.yoogurt.taxi.dal.mapper;

import com.yoogurt.taxi.dal.beans.FinanceAccount;
import tk.mybatis.mapper.common.Mapper;

public interface FinanceAccountMapper extends Mapper<FinanceAccount> {
    int saveOrUpdate(FinanceAccount account);
}