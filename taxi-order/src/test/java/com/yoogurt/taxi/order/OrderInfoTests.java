package com.yoogurt.taxi.order;

import com.yoogurt.taxi.common.pager.BasePager;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.OrderInfo;
import com.yoogurt.taxi.dal.condition.order.OrderListCondition;
import com.yoogurt.taxi.dal.condition.order.WarningOrderCondition;
import com.yoogurt.taxi.dal.model.order.OrderModel;
import com.yoogurt.taxi.order.form.PlaceOrderForm;
import com.yoogurt.taxi.order.service.OrderInfoService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderInfoTests {

    @Autowired
    private OrderInfoService orderInfoService;

    @Test
    public void orderListTest() {

        OrderListCondition condition = new OrderListCondition();
        condition.setDriverName("刘师傅");
        condition.setEndTime(new Date());
        condition.setOrderId("17101612021383517");
        condition.setPageNum(1);
        condition.setPageSize(15);
        condition.setPhone("18814892833");
        condition.setStatus(10);
        BasePager<OrderModel> pager = orderInfoService.getOrderList(condition);
        Assert.assertNotNull("获取订单列表失败", pager);
    }

    @Test
    public void warningOrdersTest() {
        WarningOrderCondition condition = new WarningOrderCondition();
        condition.setAgentUserId("17101612021383517");
        List<OrderInfo> warningOrders = orderInfoService.getWarningOrders(condition);
        Assert.assertEquals(1, warningOrders.size());
    }

    @Test
    public void placeFormTest() {
        PlaceOrderForm orderForm = new PlaceOrderForm();
        //司机发单，车主接单
//        orderForm.setRentId(17101313391940434L);
//        orderForm.setUserId(17092815473528528L);
//        orderForm.setUserType(30);
        //车主发单，司机接单
        orderForm.setRentId("17101612021383517");
        orderForm.setUserId("17101612021383517");
        orderForm.setUserType(20);
        ResponseObj obj = orderInfoService.placeOrder(orderForm);
        Assert.assertTrue(obj.getMessage(), obj.isSuccess());
    }

    @Test
    public void orderDetailsTest() {
        Map<String, Object> map = orderInfoService.getOrderDetails("17101612021383517", "17101612021383517");
        Assert.assertNotNull("订单信息不存在！", map);
    }
}