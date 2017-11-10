package com.yoogurt.taxi.account;

import com.yoogurt.taxi.account.mq.task.ChargeNotifyTaskRunner;
import com.yoogurt.taxi.account.service.FinanceAccountService;
import com.yoogurt.taxi.account.service.rest.RestUserService;
import com.yoogurt.taxi.common.bo.Money;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.FinanceAccount;
import com.yoogurt.taxi.dal.beans.UserInfo;
import com.yoogurt.taxi.dal.bo.WxNotify;
import com.yoogurt.taxi.dal.condition.account.AccountUpdateCondition;
import com.yoogurt.taxi.dal.doc.finance.Event;
import com.yoogurt.taxi.dal.doc.finance.EventTask;
import com.yoogurt.taxi.dal.enums.DestinationType;
import com.yoogurt.taxi.dal.enums.PayChannel;
import com.yoogurt.taxi.dal.enums.Payment;
import com.yoogurt.taxi.dal.enums.TradeType;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class FinanceTest {
    @Autowired
    private FinanceAccountService financeAccountService;
    @Autowired
    private RestUserService restUserService;
    @Autowired
    private ChargeNotifyTaskRunner chargeNotifyTaskRunner;

    @Test
    public void testRun() {
        EventTask eventTask = new EventTask();
        eventTask.setTaskId("1232132132121");
        Event<WxNotify> event = new Event<>();
        WxNotify notify = new WxNotify();
        notify.setOrderNo("17110110004581100");
        notify.setChannel(PayChannel.WX.getName());
        notify.setAmount(1);
        event.setData(notify);
        eventTask.setEvent(event);
        chargeNotifyTaskRunner.run(eventTask);
    }

    @Test
    public void test() {
        Long userId = 17092815464198161L;
        FinanceAccount financeAccount = financeAccountService.get(userId);
        UserInfo userInfo = restUserService.getUserInfoById(userId).getBody();
        /**充值*/
        AccountUpdateCondition condition = new AccountUpdateCondition();
        condition.setTradeType(TradeType.CHARGE);
        condition.setDestinationType(DestinationType.DEPOSIT);
        condition.setBillId(17092815834356426L);
        condition.setDraweeAccount("17364517747");
        condition.setDraweeName("吴德友");
        condition.setDraweePhone("17364517747");
        condition.setMoney(new Money("3000"));
        condition.setPayment(Payment.ALIPAY);
        condition.setTransactionNo("123123213213");
        condition.setUserId(userId);
//        /**提现*/
//        condition.setTradeType(TradeType.WITHDRAW);
//        condition.setBillId(17092815834356415L);
//        condition.setDestinationType(DestinationType.ALIPAY);
//        condition.setDraweeAccount(financeAccount.getAccountNo().toString());
//        condition.setDraweeName(userInfo.getName());
//        condition.setDraweePhone(userInfo.getUsername());
//        condition.setMoney(new Money("3000"));
//        condition.setPayeeAccount("17364517747");
//        condition.setPayeeName("吴德友");
//        condition.setPayeePhone("17364517747");
//        condition.setPayment(Payment.DEPOSIT);
//        condition.setTransactionNo("123123213213");
//        condition.setUserId(userId);
//        /**罚款*/
//        condition.setTradeType(TradeType.FINE_OUT);
//        condition.setBillId(17092815834356415L);
//        condition.setContextId(1709281583475315L);
//        condition.setDestinationType(DestinationType.BALANCE);
//        condition.setDraweeAccount(financeAccount.getAccountNo().toString());
//        condition.setDraweeName(userInfo.getName());
//        condition.setDraweePhone(userInfo.getUsername());
//        condition.setMoney(new Money("500"));
//        condition.setPayeeAccount("17364517747");
//        condition.setPayeeName("吴德友");
//        condition.setPayeePhone("17364517747");
//        condition.setPayment(Payment.BALANCE);
//        condition.setUserId(userId);
        ResponseObj responseObj = financeAccountService.updateAccount(condition);
        log.info(responseObj.toJSON());

    }
}
