package com.yoogurt.taxi.account.service;

import com.yoogurt.taxi.common.bo.Money;
import com.yoogurt.taxi.common.pager.Pager;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.FinanceBill;
import com.yoogurt.taxi.dal.condition.account.AccountUpdateCondition;
import com.yoogurt.taxi.dal.condition.account.AccountListAppCondition;
import com.yoogurt.taxi.dal.enums.BillStatus;
import com.yoogurt.taxi.dal.enums.BillType;
import com.yoogurt.taxi.dal.enums.Payment;
import com.yoogurt.taxi.dal.model.account.FinanceBillListModel;

public interface FinanceBillService {
    /**
     * app交易明细
     * @param condition
     * @return
     */
    Pager<FinanceBillListModel> getFinanceBillListApp(AccountListAppCondition condition);

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
    int save(FinanceBill bill);

    /**
     * 修改账单状态
     * @param id
     * @param billStatus
     * @return
     */
    int updateStatus(Long id, BillStatus billStatus);

    ResponseObj insertBill(Money money, AccountUpdateCondition condition, Payment payment, BillStatus billStatus, BillType billType);
}
