package com.yoogurt.taxi.notification.bo;

import com.gexin.rp.sdk.template.style.Style0;
import org.apache.commons.lang.StringUtils;

import com.gexin.rp.sdk.base.payload.APNPayload;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;

public class PushTemplate {


    /**
     * <p class="detail">
     * 功能：点击通知打开应用模板，建议安卓设备使用
     * </p>
     *
     * @param appId
     * @param appKey
     * @param title   消息标题
     * @param msg     消息体
     * @param tranMsg 透传内容，不会立即显示在APP中，通过后台程序接收并处理，通常为JSON字符串
     * @return
     * @author weihao.liu
     */
    public static final NotificationTemplate notificationTemplate(String appId, String appKey, String title, String msg, String tranMsg) {
        NotificationTemplate template = new NotificationTemplate();
        // 设置APPID与APPKEY
        template.setAppId(appId);
        template.setAppkey(appKey);

        Style0 style0 = new Style0();
        // 设置通知栏标题与内容
        style0.setTitle(title);
        style0.setText(msg);
        style0.setLogo("icon.png");

        // 设置通知是否响铃，震动，或者可清除
        //收到通知是否响铃：true响铃，false不响铃。默认响铃。
        style0.setRing(true);
        //收到通知是否振动：true振动，false不振动。默认振动。
        style0.setVibrate(true);
        //通知是否可清除：true可清除，false不可清除。默认可清除。
        style0.setClearable(true);
        template.setStyle(style0);


        // 透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动
        template.setTransmissionType(2);
        //透传内容，不支持转义字符
        template.setTransmissionContent(tranMsg);
        return template;
    }


    /**
     * <p class="detail">
     * 功能：透传消息模板
     * </p>
     *
     * @param appId
     * @param appKey
     * @param title   消息标题
     * @param msg     消息体
     * @param tranMsg 透传内容，不会立即显示在APP中，通过后台程序接收并处理，通常为JSON字符串
     * @return
     * @author weihao.liu
     * @date 2016年12月31日
     */
    public static TransmissionTemplate transmissionTemplate(String appId, String appKey, String title, String msg, String tranMsg) {
        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(appId);
        template.setAppkey(appKey);
        //收到消息是否立即启动应用：1为立即启动，2则广播等待客户端自启动
        template.setTransmissionType(2);
        //透传内容，不支持转义字符
        template.setTransmissionContent(StringUtils.isBlank(tranMsg) ? msg : tranMsg);
        try {

            APNPayload payload = new APNPayload();
            payload.setContentAvailable(1);
            payload.setSound("default");
            APNPayload.DictionaryAlertMsg alertMsg = new APNPayload.DictionaryAlertMsg();
            alertMsg.setTitle(title);
            alertMsg.setBody(msg);
            payload.setAlertMsg(alertMsg);
            template.setAPNInfo(payload);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return template;
    }

}
