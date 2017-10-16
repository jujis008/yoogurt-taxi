package com.yoogurt.taxi.notification;

import com.gexin.rp.sdk.base.IPushResult;
import com.yoogurt.taxi.dal.beans.PushDevice;
import com.yoogurt.taxi.dal.enums.DeviceType;
import com.yoogurt.taxi.dal.enums.MsgType;
import com.yoogurt.taxi.notification.config.GeTuiConfig;
import com.yoogurt.taxi.notification.helper.PushHelper;
import com.yoogurt.taxi.notification.service.PushService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PushTests {

    @Autowired
    private PushService pushService;

    @Autowired
    private PushHelper pushHelper;

    @Autowired
    private GeTuiConfig getui;

    @Test
    public void bindDeviceTest() {
        PushDevice device = pushService.userBinding(8888L, "cl_097oKJw5ur1o2pi09AS");
        Assert.assertNotNull("绑定失败", device);
    }

    @Test
    public void pushTest() {
        try {
            IPushResult push = pushHelper.push(MsgType.ALL, DeviceType.ANDROID, "", getui);
            push.getResponse();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
