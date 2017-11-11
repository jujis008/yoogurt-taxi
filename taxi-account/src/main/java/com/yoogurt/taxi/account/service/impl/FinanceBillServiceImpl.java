package com.yoogurt.taxi.account.service.impl;

import com.yoogurt.taxi.account.dao.FinanceBillDao;
import com.yoogurt.taxi.account.service.FinanceAccountService;
import com.yoogurt.taxi.account.service.FinanceBillService;
import com.yoogurt.taxi.account.service.FinanceRecordService;
import com.yoogurt.taxi.account.service.rest.RestUserService;
import com.yoogurt.taxi.common.bo.Money;
import com.yoogurt.taxi.common.enums.MessageQueue;
import com.yoogurt.taxi.common.helper.excel.BankReceiptOfMerchantsModel;
import com.yoogurt.taxi.common.pager.Pager;
import com.yoogurt.taxi.common.utils.RandomUtils;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.common.vo.RestResult;
import com.yoogurt.taxi.dal.beans.FinanceBill;
import com.yoogurt.taxi.dal.beans.FinanceRecord;
import com.yoogurt.taxi.dal.beans.UserInfo;
import com.yoogurt.taxi.dal.condition.account.*;
import com.yoogurt.taxi.dal.enums.*;
import com.yoogurt.taxi.dal.model.account.FinanceBillListAppModel;
import com.yoogurt.taxi.dal.model.account.FinanceBillListWebModel;
import com.yoogurt.taxi.dal.model.account.WithdrawBillDetailModel;
import com.yoogurt.taxi.dal.model.account.WithdrawBillListWebModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class FinanceBillServiceImpl implements FinanceBillService {
    @Autowired
    private FinanceBillDao financeBillDao;
    @Autowired
    private FinanceRecordService financeRecordService;
    @Autowired
    private RestUserService restUserService;
    @Autowired
    private FinanceAccountService financeAccountService;
    @Override
    public Pager<FinanceBillListAppModel> getFinanceBillListApp(AccountListAppCondition condition) {
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
        FinanceRecord record = new FinanceRecord();
        record.setStatus(billStatus.getCode());
        record.setBillNo(financeBill.getBillNo());
        record.setBillId(financeBill.getId());
        record.setRemark("后台操作更改");
        financeRecordService.save(record);
        return financeBillDao.updateById(financeBill);
    }

    @Override
    public FinanceBill getFinanceBillByBillNo(String billNo) {
        Example example = new Example(FinanceBill.class);
        example.createCriteria().andEqualTo("billNo",billNo)
                .andEqualTo("billStatus",BillStatus.PENDING.getCode());
        List<FinanceBill> financeBillList = financeBillDao.selectByExample(example);
        if (CollectionUtils.isEmpty(financeBillList)) {
            return null;
        }
        return financeBillList.get(0);
    }

    @Transactional
    @Override
    public int chargeSuccessOrFailure(String billNo, BillStatus billStatus) {
        FinanceBill financeBill = getFinanceBillByBillNo(billNo);
        if (financeBill == null) {
            return 0;
        }
        if (billStatus == BillStatus.SUCCESS) {

            AccountUpdateCondition condition = new AccountUpdateCondition();
            condition.setPayment(Payment.getEnumsBycode(financeBill.getPayment()));
            condition.setDestinationType(DestinationType.getEnumsBycode(financeBill.getDestinationType()));
            condition.setTradeType(TradeType.CHARGE);
            condition.setUserId(financeBill.getUserId());
            condition.setMoney(new Money(financeBill.getAmount()));
            condition.setChangeType(null);
            condition.setBillId(financeBill.getId());
            condition.setDraweeAccount(financeBill.getDraweeAccount());
            condition.setDraweeName(financeBill.getDraweeName());
            condition.setDraweePhone(financeBill.getDraweePhone());
            condition.setPayeeAccount(financeBill.getPayeeAccount());
            condition.setPayeeName(financeBill.getPayeeName());
            condition.setPayeePhone(financeBill.getPayeePhone());
            condition.setContextId(billNo);
            financeAccountService.updateAccount(condition);
            log.info("充值成功："+billNo);

        }
        if (billStatus == BillStatus.FAIL) {
            financeBillDao.deleteById(financeBill.getId());
            financeRecordService.deleteByBillNo(billNo);
            log.info("充值取消："+billNo);
        }
        return 1;
    }

    @Override
    @Transactional
    public ResponseObj insertBill(Money money, AccountUpdateCondition condition, Payment payment, BillStatus billStatus, BillType billType) {
        RestResult<UserInfo> userInfoRestResult = restUserService.getUserInfoById(condition.getUserId());
        if (!userInfoRestResult.isSuccess()) {
            return ResponseObj.fail(userInfoRestResult.getStatus(),userInfoRestResult.getMessage());
        }
        UserInfo userInfo = userInfoRestResult.getBody();
        FinanceBill financeBill = new FinanceBill();
        financeBill.setAmount(money.getAmount());
        String billNo = RandomUtils.getPrimaryKey();
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
        financeBill.setBankName(condition.getBankName());
        financeBill.setBankAddress(condition.getBankAddress());

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
        Map<String, Object> map = new HashMap<>();
        map.put("billNo",billNo);
        map.put("subject","替你开-押金充值");
        map.put("body","["+financeBill.getBillNo()+"]押金充值￥" + money.getAmount().doubleValue());
        map.put("metadata", new HashMap<String, Object>() {{
            put("biz", MessageQueue.CHARGE_NOTIFY_QUEUE.getBiz());
        }});
        return ResponseObj.success(map);
    }

    @Override
    public Pager<FinanceBillListWebModel> getFinanceBillListWeb(BillListWebCondition condition) {
        return financeBillDao.getFinanceBillListWeb(condition);
    }

    @Override
    public Pager<WithdrawBillListWebModel> getWithdrawBillListWeb(WithdrawListWebCondition condition) {
        condition.setTradeType(TradeType.WITHDRAW.getCode());
        return financeBillDao.getWithdrawBillListWeb(condition);
    }

    @Override
    public WithdrawBillDetailModel getWithdrawBillDetail(Long billId) {
        FinanceBill financeBill = financeBillDao.selectById(billId);
        WithdrawBillDetailModel model = new WithdrawBillDetailModel();
        try {
            BeanUtils.copyProperties(model,financeBill);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return model;
    }

    @Override
    public List<FinanceBill> getBillList(BillCondition condition) {
        Example example = new Example(FinanceBill.class);
        example.createCriteria().andEqualTo("userId",condition.getUserId())
                .andEqualTo("isDeleted",Boolean.FALSE)
                .andBetween("gmtCreate",condition.getStartTime(),condition.getEndTime());
        return financeBillDao.selectByExample(example);
    }

    @Override
    public List<Map<String,Object>> getBillListForExport(ExportBillCondition condition) {
        return financeBillDao.getWithdrawListForExport(condition);
    }

    @Override
    public ResponseObj batchHandleWithdraw(List<BankReceiptOfMerchantsModel> list) {
        int count=0;
        for (BankReceiptOfMerchantsModel model:list) {
            Long billId = model.getId();
            FinanceBill financeBill = financeBillDao.selectById(billId);
            if (financeBill == null) {
                log.error("账单id："+billId+",账单记录不存在，请核实");
                continue;
            }
            if (!financeBill.getBillStatus().equals(BillStatus.PENDING.getCode())) {
                log.error("账单id："+billId+",账单异常billStatus="+financeBill.getBillStatus());
                continue;
            }
            if (financeBill.getAmount().compareTo(model.getAmount())!=0) {
                log.error("账单id："+billId+",金额有误");
                continue;
            }
            if (!financeBill.getPayeeAccount().equals(model.getAccountNo())) {
                log.error("账单id："+billId+",账号有误");
                continue;
            }
            if (!financeBill.getPayeeName().equals(model.getAccountName())) {
                log.error("账单id："+billId+",户名有误");
            }
            BillStatus billStatus = model.getStatus()?BillStatus.SUCCESS:BillStatus.FAIL;
            financeAccountService.handleWithdraw(billId,billStatus);
            count++;
        }
        return ResponseObj.success(count);
    }

}
