package com.yoogurt.taxi.order;

import com.yoogurt.taxi.dal.beans.CommentTagStatistic;
import com.yoogurt.taxi.order.form.CommentForm;
import com.yoogurt.taxi.order.service.CommentTagStatisticService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class CommentTagStatisticTests {

    @Autowired
    private CommentTagStatisticService statisticService;


    @Test
    public void recordTest() {
        statisticService.record(8888L, new Long[]{1L, 2L}, new String[]{"驾驶平稳", "爱护车辆"});
    }

    @Test
    public void getStatisticTest() {
        List<CommentTagStatistic> statistics = statisticService.getStatistic(8888L);
        Assert.assertEquals(2, statistics.size());
    }
}