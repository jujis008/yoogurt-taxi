package com.yoogurt.taxi.order;

import com.yoogurt.taxi.dal.beans.OrderHandoverRule;
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
    private HandoverRuleService ruleService;

    @Test
    public void ruleInfoTest() {
        OrderHandoverRule rule = ruleService.getRuleInfo();
        Assert.assertNotNull("交车违约规则不能为空", rule);
    }

    @Test
    public void ruleIntroductionTest() {
        String introduction = ruleService.getIntroduction();
        Assert.assertNotNull("交车违约规则说明不能为空", introduction);
        log.info(introduction);
    }

    @Test
    public void ruleInfoByTimeTest() {
        OrderHandoverRule rule = ruleService.getRuleInfo(5, "MINUTES");
        Assert.assertNotNull("交车违约规则不能为空", rule);
    }
}
