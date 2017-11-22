package com.yoogurt.taxi.account.service;

import com.yoogurt.taxi.common.bo.Money;
import com.yoogurt.taxi.common.helper.excel.BankReceiptOfMerchantsModel;
import com.yoogurt.taxi.common.pager.BasePager;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.FinanceBill;
import com.yoogurt.taxi.dal.condition.account.*;
import com.yoogurt.taxi.dal.enums.BillStatus;
import com.yoogurt.taxi.dal.enums.BillType;
import com.yoogurt.taxi.dal.enums.Payment;
import com.yoogurt.taxi.dal.model.account.FinanceBillListAppModel;
import com.yoogurt.taxi.dal.model.account.FinanceBillListWebModel;
import com.yoogurt.taxi.dal.model.account.WithdrawBillDetailModel;
import com.yoogurt.taxi.dal.model.account.WithdrawBillListWebModel;

import java.util.List;
import java.util.Map;

public interface FinanceBillService {
    /**
     * app交易明细
     * @param condition
     * @return
     */
    BasePager<FinanceBillListAppModel> getFinanceBillListApp(AccountListAppCondition condition);

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

    FinanceBill getFinanceBillByBillNo(String billNo);

    int chargeSuccessOrFailure(String billNo, BillStatus billStatus);

    ResponseObj insertBill(Money money, AccountUpdateCondition condition, Payment payment, BillStatus billStatus, BillType billType);

    BasePager<FinanceBillListWebModel> getFinanceBillListWeb(BillListWebCondition condition);

    BasePager<WithdrawBillListWebModel> getWithdrawBillListWeb(WithdrawListWebCondition condition);

    WithdrawBillDetailModel getWithdrawBillDetail(Long billId);

    List<FinanceBill> getBillList(BillCondition condition);

    List<Map<String,Object>> getBillListForExport(ExportBillCondition condition);

    ResponseObj batchHandleWithdraw(List<BankReceiptOfMerchantsModel> list);
}
