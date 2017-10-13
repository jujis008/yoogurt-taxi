package com.yoogurt.taxi.account;

import com.yoogurt.taxi.account.service.FinanceAccountService;
import com.yoogurt.taxi.account.service.rest.RestUserService;
import com.yoogurt.taxi.common.bo.Money;
import com.yoogurt.taxi.common.vo.RestResult;
import com.yoogurt.taxi.dal.beans.FinanceAccount;
import com.yoogurt.taxi.dal.beans.UserInfo;
import com.yoogurt.taxi.dal.condition.account.AccountUpdateCondition;
import com.yoogurt.taxi.dal.enums.BillType;
import com.yoogurt.taxi.dal.enums.DestinationType;
import com.yoogurt.taxi.dal.enums.Payment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FinanceTest {
    @Autowired
    private FinanceAccountService financeAccountService;
    @Autowired
    private RestUserService restUserService;

    @Test
    public void test() {
        Long userId = 17092815464356415L;
        FinanceAccount financeAccount = financeAccountService.get(userId);
        UserInfo userInfo = restUserService.getUserInfoById(userId).getBody();
        //充值
        AccountUpdateCondition condition = new AccountUpdateCondition();
        condition.setBillType(BillType.CHARGE);
        condition.setBillId(17092815834356415L);
        condition.setDestinationType(DestinationType.DEPOSIT);
        condition.setDraweeAccount("17364517747");
        condition.setDraweeName("吴德友");
        condition.setDraweePhone("17364517747");
        condition.setMoney(new Money("3000"));
        condition.setPayeeAccount(financeAccount.getAccountNo().toString());
        condition.setPayeeName(userInfo.getName());
        condition.setPayeePhone(userInfo.getUsername());
        condition.setPayment(Payment.ALIPAY);
        condition.setTransactionNo("123123213213");
        financeAccountService.updateAccount(condition);
    }
}
