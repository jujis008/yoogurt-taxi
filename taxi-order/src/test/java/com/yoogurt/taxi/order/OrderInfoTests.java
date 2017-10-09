package com.yoogurt.taxi.order;

import com.yoogurt.taxi.common.pager.Pager;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.condition.order.OrderListCondition;
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
        condition.setOrderId(1745556817324L);
        condition.setPageNum(1);
        condition.setPageSize(15);
        condition.setPhone("18814892833");
        condition.setStatus(10);
        Pager<OrderModel> pager = orderInfoService.getOrderList(condition);
        Assert.assertNotNull("获取订单列表失败", pager);
    }

    @Test
    public void placeFormTest() {
        PlaceOrderForm orderForm = new PlaceOrderForm();
        orderForm.setRentId(17092620025316058L);
        orderForm.setUserId(8888L);
        orderForm.setUserType(20);
        ResponseObj obj = orderInfoService.placeOrder(orderForm);
        Assert.assertTrue(obj.getMessage(), obj.isSuccess());
    }

    @Test
    public void orderDetailsTest() {
        OrderModel orderDetails = orderInfoService.getOrderDetails(17092615073534929L);
        Assert.assertNotNull("订单信息不存在！", orderDetails);
    }
}