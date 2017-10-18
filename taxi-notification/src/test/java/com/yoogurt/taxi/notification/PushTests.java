package com.yoogurt.taxi.notification;

import com.gexin.rp.sdk.base.IPushResult;
import com.yoogurt.taxi.common.constant.Constants;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.PushDevice;
import com.yoogurt.taxi.dal.enums.DeviceType;
import com.yoogurt.taxi.dal.enums.MsgType;
import com.yoogurt.taxi.dal.enums.UserType;
import com.yoogurt.taxi.notification.config.IGeTuiConfig;
import com.yoogurt.taxi.notification.factory.GeTuiFactory;
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
        ResponseObj obj = pushService.pushMessage(UserType.USER_APP_OFFICE, DeviceType.ANDROID.getType(), Constants.OFFICIAL_APP_NAME, "小胖在吗？去吃KFC吧！", true);
        Assert.assertTrue(obj.getMessage(), obj.isSuccess());
    }

    @Test
    public void getuiConfigTest() {
        IGeTuiConfig config = GeTuiFactory.getConfigFactory(UserType.USER_APP_OFFICE).generateConfig();
        Assert.assertNotNull("获取推送配置信息失败", config);
    }
}
