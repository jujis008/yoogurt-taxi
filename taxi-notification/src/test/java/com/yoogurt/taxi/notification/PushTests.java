package com.yoogurt.taxi.notification;

import com.gexin.rp.sdk.base.IPushResult;
import com.yoogurt.taxi.dal.beans.PushDevice;
import com.yoogurt.taxi.dal.enums.DeviceType;
import com.yoogurt.taxi.dal.enums.MsgType;
import com.yoogurt.taxi.notification.config.GeTuiConfig20;
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
    private GeTuiConfig20 getui;

    @Test
    public void bindDeviceTest() {
        PushDevice device = pushService.binding("p0j_097oKJw5ur1o2pi09KS", 8888L);
        Assert.assertNotNull("绑定失败", device);
    }

    @Test
    public void unbindDeviceTest() {
        PushDevice device = pushService.unBinding("p0j_097oKJw5ur1o2pi09KS", 8888L);
        Assert.assertNotNull("解绑失败", device);
        Assert.assertNull("解绑失败", device.getUserId());
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
