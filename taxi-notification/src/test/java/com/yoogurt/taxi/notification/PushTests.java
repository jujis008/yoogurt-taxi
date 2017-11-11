package com.yoogurt.taxi.notification;

import com.yoogurt.taxi.common.constant.Constants;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.PushDevice;
import com.yoogurt.taxi.dal.bo.PushPayload;
import com.yoogurt.taxi.dal.enums.SendType;
import com.yoogurt.taxi.dal.enums.UserType;
import com.yoogurt.taxi.notification.config.getui.IGeTuiConfig;
import com.yoogurt.taxi.notification.factory.GeTuiFactory;
import com.yoogurt.taxi.notification.form.UserBindingForm;
import com.yoogurt.taxi.notification.service.PushService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PushTests {

    @Autowired
    private PushService pushService;

    @Test
    public void bindDeviceTest() {
        UserBindingForm form = new UserBindingForm();
        form.setClientId("Ko0Pkj980Wsz3yyj9ki7098mcn932");
        form.setDeviceName("ONE MX4");
        form.setDeviceType("Android");
        form.setOsVersion("6.1.9");
        form.setUserId("8888");
        PushDevice device = pushService.binding(form);
        Assert.assertNotNull("绑定失败", device);
        Assert.assertEquals("解绑失败", "8888", device.getUserId());
    }

    @Test
    public void unbindDeviceTest() {
        PushDevice device = pushService.unBinding("p0j_097oKJw5ur1o2pi09KS", "8888");
        Assert.assertNotNull("解绑失败", device);
        Assert.assertNull("解绑失败", device.getUserId());
    }

    @Test
    public void pushTest() {
        String message = SendType.ORDER_FINISH.getMessage();
        PushPayload payload = new PushPayload(UserType.USER_APP_OFFICE, SendType.ORDER_FINISH, Constants.OFFICIAL_APP_NAME, String.format(message, 17101609512257244L));
        payload.setExtras(new HashMap<String, Object>(){{
            put("orderId", 17101609512257244L);
        }});
        payload.addUserId("8888");
        ResponseObj obj = pushService.pushMessage(payload.getUserIds(), payload.getUserType(), payload.getSendType(), payload.getMsgType(), payload.getDeviceType(), payload.getTitle(), payload.getContent(), payload.getExtras(), true);
        Assert.assertTrue(obj.getMessage(), obj.isSuccess());
    }

    @Test
    public void getuiConfigTest() {
        IGeTuiConfig config = GeTuiFactory.getConfigFactory(UserType.USER_APP_OFFICE).generateConfig();
        Assert.assertNotNull("获取推送配置信息失败", config);
    }
}
