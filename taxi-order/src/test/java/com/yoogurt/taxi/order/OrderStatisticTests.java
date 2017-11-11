package com.yoogurt.taxi.order;

import com.yoogurt.taxi.order.form.OrderStatisticForm;
import com.yoogurt.taxi.order.service.OrderStatisticService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderStatisticTests {

    @Autowired
    private OrderStatisticService statisticService;

    @Test
    public void recordTest() {
        OrderStatisticForm form = OrderStatisticForm.builder().orderCount(1).disobeyCount(1).userId("17101612021383517").build();
        statisticService.record(form);
    }
}
