package com.yoogurt.taxi.account.service.impl;

import com.yoogurt.taxi.account.dao.FinanceBillDao;
import com.yoogurt.taxi.account.service.FinanceBillService;
import com.yoogurt.taxi.account.service.FinanceRecordService;
import com.yoogurt.taxi.common.bo.Money;
import com.yoogurt.taxi.common.utils.RandomUtils;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.FinanceBill;
import com.yoogurt.taxi.dal.beans.FinanceRecord;
import com.yoogurt.taxi.dal.beans.UserInfo;
import com.yoogurt.taxi.dal.condition.account.AccountUpdateCondition;
import com.yoogurt.taxi.dal.condition.account.RecordListAppCondition;
import com.yoogurt.taxi.dal.enums.BillStatus;
import com.yoogurt.taxi.dal.enums.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FinanceBillServiceImpl implements FinanceBillService {
    @Autowired
    private FinanceBillDao financeBillDao;
    @Autowired
    private FinanceRecordService financeRecordService;
    @Override
    public ResponseObj getFinanceBillListApp(RecordListAppCondition condition) {
        return null;
    }

    @Override
    public FinanceBill get(Long id) {
        return financeBillDao.selectById(id);
    }

    @Override
    public ResponseObj save(FinanceBill bill) {
        if (bill.getId() != null) {
            financeBillDao.updateById(bill);
        }
        financeBillDao.insert(bill);
        return ResponseObj.success(bill);
    }

    @Override
    public ResponseObj updateStatus(Long id, BillStatus status) {
        return null;
    }

    @Override
    public void insertBill(Money money, AccountUpdateCondition condition, Payment payment, UserInfo userInfo, BillStatus billStatus) {
        FinanceBill financeBill = new FinanceBill();
        financeBill.setAmount(money.getAmount());
        Long billNo = RandomUtils.getPrimaryKey();
        financeBill.setBillNo(billNo);
        financeBill.setContextId(condition.getContextId());
        /**付款信息*/
        financeBill.setPayment(payment.getCode());
        financeBill.setDraweeAccount(condition.getDraweeAccount());
        financeBill.setDraweeName(condition.getDraweeName());
        financeBill.setDraweePhone(condition.getDraweePhone());

        /**用户信息*/
        financeBill.setUserId(userInfo.getUserId());
        financeBill.setUsername(userInfo.getUsername());
        financeBill.setUserType(userInfo.getType());
        financeBill.setName(userInfo.getName());

        /**收款信息*/
        financeBill.setDestinationType(condition.getDestinationType().getCode());
        financeBill.setPayeeAccount(condition.getPayeeAccount());
        financeBill.setPayeeName(condition.getPayeeName());
        financeBill.setPayeePhone(condition.getPayeePhone());

        financeBill.setStatus(billStatus.getCode());
        financeBill.setType(condition.getBillType().getCode());

        /**2.插入账单*/
        financeBillDao.insert(financeBill);

        FinanceRecord financeRecord = new FinanceRecord(financeBill.getId(), financeBill.getBillNo(), billStatus.getCode(), null);
        financeRecordService.save(financeRecord);
    }
}
