package com.yoogurt.taxi.order;

import com.yoogurt.taxi.dal.beans.OrderGiveBackRule;
import com.yoogurt.taxi.dal.beans.OrderHandoverRule;
import com.yoogurt.taxi.order.service.GiveBackRuleService;
import com.yoogurt.taxi.order.service.HandoverRuleService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class DisobeyRuleTests {

    @Autowired
    private HandoverRuleService handoverRuleService;

    @Autowired
    private GiveBackRuleService giveBackRuleService;

    @Test
    public void ruleInfoTest() {
        OrderHandoverRule rule = handoverRuleService.getRuleInfo();
        Assert.assertNotNull("交车违约规则不能为空", rule);

        OrderGiveBackRule giveBackRule = giveBackRuleService.getRuleInfo();
        Assert.assertNotNull("还车违约规则不能为空", giveBackRule);

    }

    @Test
    public void ruleIntroductionTest() {
        String introduction = handoverRuleService.getIntroduction();
        Assert.assertNotNull("交车违约规则说明不能为空", introduction);
        log.info(introduction);

        String introduction1 = giveBackRuleService.getIntroduction();
        Assert.assertNotNull("还车违约规则说明不能为空", introduction);
        log.info(introduction1);
    }

    @Test
    public void ruleInfoByTimeTest() {
        OrderHandoverRule rule = handoverRuleService.getRuleInfo(5, "MINUTES");
        Assert.assertNotNull("交车违约规则不能为空", rule);

        OrderGiveBackRule rule1 = giveBackRuleService.getRuleInfo(5, "MINUTES");
        Assert.assertNotNull("交车违约规则不能为空", rule1);
    }


}
