package com.yoogurt.taxi.order;

import com.yoogurt.taxi.dal.beans.CommentTagStatistic;
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
        statisticService.record("17101612021383517", new Long[]{1L, 2L});
    }

    @Test
    public void getStatisticTest() {
        List<CommentTagStatistic> statistics = statisticService.getStatistic("17101612021383517");
        Assert.assertEquals(2, statistics.size());
    }
}
