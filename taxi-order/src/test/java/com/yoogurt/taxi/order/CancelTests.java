package com.yoogurt.taxi.order;

import com.yoogurt.taxi.dal.enums.ResponsibleParty;
import com.yoogurt.taxi.dal.model.order.CancelOrderModel;
import com.yoogurt.taxi.order.form.CancelForm;
import com.yoogurt.taxi.order.service.CancelService;
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
public class CancelTests {

    @Autowired
    private CancelService cancelService;

    @Test
    public void doCancelTest() {
        CancelForm cancelForm = new CancelForm();
        cancelForm.setOrderId(17092711405325650L);
        cancelForm.setReason("不要脸");
        cancelForm.setResponsibleParty(ResponsibleParty.AGENT.getCode());
        cancelForm.setFromApp(false);
        CancelOrderModel model = cancelService.doCancel(cancelForm);
        Assert.assertNotNull("取消失败！", model);
    }
}
