package com.yoogurt.taxi.account.service;

import com.yoogurt.taxi.common.bo.Money;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.FinanceBill;
import com.yoogurt.taxi.dal.beans.UserInfo;
import com.yoogurt.taxi.dal.condition.account.AccountUpdateCondition;
import com.yoogurt.taxi.dal.condition.account.RecordListAppCondition;
import com.yoogurt.taxi.dal.enums.BillStatus;
import com.yoogurt.taxi.dal.enums.Payment;

public interface FinanceBillService {
    /**
     * app交易明细
     * @param condition
     * @return
     */
    ResponseObj getFinanceBillListApp(RecordListAppCondition condition);

    /**
     * 获取financeBill对象
     * @param id
     * @return
     */
    FinanceBill get(Long id);

    /**
     * 新增/编辑
     * @param bill
     * @return
     */
    ResponseObj save(FinanceBill bill);

    /**
     * 修改账单状态
     * @param id
     * @param status
     * @return
     */
    ResponseObj updateStatus(Long id, BillStatus status);

    void insertBill(Money money, AccountUpdateCondition condition, Payment payment, UserInfo userInfo, BillStatus billStatus);
}
