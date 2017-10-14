package com.yoogurt.taxi.account.service.impl;

import com.yoogurt.taxi.account.dao.FinanceBillDao;
import com.yoogurt.taxi.account.service.FinanceBillService;
import com.yoogurt.taxi.account.service.FinanceRecordService;
import com.yoogurt.taxi.account.service.rest.RestUserService;
import com.yoogurt.taxi.common.bo.Money;
import com.yoogurt.taxi.common.pager.Pager;
import com.yoogurt.taxi.common.utils.RandomUtils;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.common.vo.RestResult;
import com.yoogurt.taxi.dal.beans.FinanceBill;
import com.yoogurt.taxi.dal.beans.FinanceRecord;
import com.yoogurt.taxi.dal.beans.UserInfo;
import com.yoogurt.taxi.dal.condition.account.AccountUpdateCondition;
import com.yoogurt.taxi.dal.condition.account.RecordListAppCondition;
import com.yoogurt.taxi.dal.enums.BillStatus;
import com.yoogurt.taxi.dal.enums.BillType;
import com.yoogurt.taxi.dal.enums.Payment;
import com.yoogurt.taxi.dal.model.account.FinanceBillListModel;
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
    @Autowired
    private RestUserService restUserService;
    @Override
    public Pager<FinanceBillListModel> getFinanceBillListApp(RecordListAppCondition condition) {
        return financeBillDao.getFinanceBillListApp(condition);
    }

    @Override
    public FinanceBill get(Long id) {
        return financeBillDao.selectById(id);
    }

    @Override
    public int save(FinanceBill bill) {
        if (bill.getId() != null) {
            return financeBillDao.updateById(bill);
        }
        return financeBillDao.insert(bill);
    }

    @Override
    public int updateStatus(Long id, BillStatus billStatus) {
        FinanceBill financeBill = get(id);
        if (financeBill == null) {
            return 0;
        }
        financeBill.setBillStatus(billStatus.getCode());
        return financeBillDao.updateById(financeBill);
    }

    @Override
    public ResponseObj insertBill(Money money, AccountUpdateCondition condition, Payment payment, BillStatus billStatus, BillType billType) {
        RestResult<UserInfo> userInfoRestResult = restUserService.getUserInfoById(condition.getUserId());
        if (!userInfoRestResult.isSuccess()) {
            return ResponseObj.fail(userInfoRestResult.getStatus(),userInfoRestResult.getMessage());
        }
        UserInfo userInfo = userInfoRestResult.getBody();
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

        financeBill.setBillStatus(billStatus.getCode());
        financeBill.setBillType(billType.getCode());
        financeBill.setTradeType(condition.getTradeType().getCode());

        /**2.插入账单*/
        financeBillDao.insert(financeBill);

        FinanceRecord financeRecord = new FinanceRecord();
        financeRecord.setStatus(billStatus.getCode());
        financeRecord.setBillNo(financeBill.getBillNo());
        financeRecord.setBillId(financeBill.getId());
        financeRecordService.save(financeRecord);
        return ResponseObj.success(billNo);
    }
}
