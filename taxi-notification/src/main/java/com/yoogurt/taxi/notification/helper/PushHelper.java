package com.yoogurt.taxi.notification.helper;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.AppMessage;
import com.gexin.rp.sdk.base.impl.ListMessage;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.base.uitls.AppConditions;
import com.gexin.rp.sdk.exceptions.RequestException;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.yoogurt.taxi.dal.enums.DeviceType;
import com.yoogurt.taxi.dal.enums.MsgType;
import com.yoogurt.taxi.notification.config.GeTuiConfig;
import com.yoogurt.taxi.notification.bo.PushTemplate;
import com.yoogurt.taxi.notification.bo.Transmission;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class PushHelper {
	
	/** 离线消息有效时间（ms）*/
	private static final long OFFLINE_EXPIRE_TIME = 24 * 3600 * 1000;
	
	/**
	 * <p class="detail">
	 * 功能：消息推送入口方法
	 * </p>
	 * @author weihao.liu
	 * @date 2016年12月31日 
	 * @param msgType
	 * @param deviceType
	 * @param clientId
	 * @param getui
	 * @return
	 * @throws IOException
	 */
	public IPushResult push(MsgType msgType, DeviceType deviceType, String clientId, GeTuiConfig getui) throws IOException{
		if(msgType != null && msgType.equals(MsgType.SINGLE) && StringUtils.isNotBlank(clientId)){
			return singlePushByTransmission(clientId, getui);
		}else{
			switch (deviceType) {
				case ALL:
					return pushAll(getui);

				case ANDROID:
					return pushAllAndroid(getui);

				case IOS:
					return pushAllIOS(getui);

				default:
					return null;
			}
		}
	}
	
	/**
	 * <p class="detail">
	 * 功能：批量推送给指定的用户群
	 * </p>
	 * @author weihao.liu
	 * @date 2017年1月20日 
	 * @param clientIds
	 * @param getui
	 * @return
	 * @throws IOException
	 */
	public IPushResult push(List<String> clientIds, GeTuiConfig getui) throws IOException{
		IGtPush push = new IGtPush(getui.getHost(), getui.getAppKey(), getui.getMaster());
	    push.connect();
	    String tranContent = getui.getContent();
	    Transmission transmission = getui.getTransmission();
	    if(transmission != null){
	    	tranContent = transmission.toJSON();
	    }
	    TransmissionTemplate template = PushTemplate.transmissionTemplate(getui.getAppId(), getui.getAppKey(), getui.getTitle(), getui.getContent(), tranContent);
	    ListMessage message = new ListMessage(); 
	    message.setOffline(true);
	    message.setOfflineExpireTime(OFFLINE_EXPIRE_TIME);
	    message.setPushNetWorkType(0);
	    message.setData(template);
	    List<Target> targets = new ArrayList<Target>();
	    for(String clientId:clientIds){
		    Target target = new Target();
		    target.setAppId(getui.getAppId());
	        target.setClientId(clientId);
	        targets.add(target);
	    }
	    IPushResult ret = null;
        try {
        	// taskId用于在推送时去查找对应的message
            String taskId = push.getContentId(message);
            ret = push.pushMessageToList(taskId, targets);
        } catch (RequestException e) {
            e.printStackTrace();
        }
        return ret;
	}
	/**
	 * <p class="detail">
	 * 功能：向所有设备推送
	 * </p>
	 * @author weihao.liu
	 * @date 2016年12月31日 
	 * @param getui
	 * @return
	 * @throws IOException
	 */
	public IPushResult pushAll(GeTuiConfig getui) throws IOException{
		IGtPush push = new IGtPush(getui.getHost(), getui.getAppKey(), getui.getMaster());
		//建立连接，开始鉴权
		push.connect();
		String tranContent = getui.getContent();
	    Transmission transmission = getui.getTransmission();
	    if(transmission != null){
	    	tranContent = transmission.toJSON();
	    }
		//通知模板
		NotificationTemplate template = PushTemplate.notificationTemplate(getui.getAppId(), getui.getAppKey(), getui.getTitle(), getui.getContent(), tranContent);
		AppMessage message = new AppMessage();
		message.setData(template);
		//设置消息离线，并设置离线时间
		message.setOffline(true);
        //离线有效时间，单位为毫秒，可选
		message.setOfflineExpireTime(OFFLINE_EXPIRE_TIME);
		//设置推送目标条件过滤
		List<String> appIdList = new ArrayList<>();
        appIdList.add(getui.getAppId());
		message.setAppIdList(appIdList);
		return push.pushMessageToApp(message);
	}
	/**
	 * <p class="detail">
	 * 功能：向所有安卓设备推送通知消息
	 * </p>
	 * @author weihao.liu
	 * @date 2016年12月31日 
	 * @param getui
	 * @return
	 * @throws IOException
	 */
	public IPushResult pushAllAndroid(GeTuiConfig getui) throws IOException{
		IGtPush push = new IGtPush(getui.getHost(), getui.getAppKey(), getui.getMaster());
		//建立连接，开始鉴权
		push.connect();
		String tranContent = getui.getContent();
	    Transmission transmission = getui.getTransmission();
	    if(transmission != null){
	    	tranContent = transmission.toJSON();
	    }
		//通知模板
		NotificationTemplate template = PushTemplate.notificationTemplate(getui.getAppId(), getui.getAppKey(), getui.getTitle(), getui.getContent(), tranContent);
		AppMessage message = new AppMessage();
		message.setData(template);
		//设置消息离线，并设置离线时间
		message.setOffline(true);
        //离线有效时间，单位为毫秒，可选
		message.setOfflineExpireTime(OFFLINE_EXPIRE_TIME);
	    //推送给App的目标用户需要满足的条件
        AppConditions cdt = new AppConditions(); 
		//设置推送目标条件过滤
		List<String> appIdList = new ArrayList<>();
        appIdList.add(getui.getAppId());
		message.setAppIdList(appIdList);
		  //手机类型
        List<String> phoneTypeList = new ArrayList<>();
        phoneTypeList.add("ANDROID");
		cdt.addCondition(AppConditions.PHONE_TYPE, phoneTypeList);
		message.setConditions(cdt);
		return push.pushMessageToApp(message);
	}
	
	/**
	 * <p class="detail">
	 * 功能：向所有iOS设备推送透传消息
	 * </p>
	 * @author weihao.liu
	 * @date 2016年12月31日 
	 * @param getui
	 * @return
	 * @throws IOException
	 */
	public IPushResult pushAllIOS(GeTuiConfig getui) throws IOException {
		IGtPush push = new IGtPush(getui.getHost(), getui.getAppKey(), getui.getMaster());
	    push.connect();
	    String tranContent = getui.getContent();
	    Transmission transmission = getui.getTransmission();
	    if(transmission != null){
	    	tranContent = transmission.toJSON();
	    }
	    TransmissionTemplate template = PushTemplate.transmissionTemplate(getui.getAppId(), getui.getAppKey(), getui.getTitle(), getui.getContent(), tranContent);
	    AppMessage message = new AppMessage();
	    message.setOffline(true);
	    message.setOfflineExpireTime(OFFLINE_EXPIRE_TIME);
	    message.setPushNetWorkType(0);
	    
	    message.setData(template);
	    List<String> appIdList = new ArrayList<>();
	    appIdList.add(getui.getAppId());
	    message.setAppIdList(appIdList);
	    //推送给App的目标用户需要满足的条件
        AppConditions cdt = new AppConditions(); 
        //手机类型
        List<String> phoneTypeList = new ArrayList<>();
        phoneTypeList.add("IOS");
		cdt.addCondition(AppConditions.PHONE_TYPE, phoneTypeList);
		message.setConditions(cdt);
	    return push.pushMessageToApp(message);
	}
	
	
	 /**
	  * <p class="detail">
	  * 功能：单个推送使用透传推送！(苹果安卓都可以用) 替代安卓单个推送和苹果单个推送
	  * </p>
	  * @author weihao.liu
	  * @date 2016年12月31日 
	  * @param clientId
	  * @param getui
	  * @return
	  * @throws IOException
	  */
	public IPushResult singlePushByTransmission(String clientId, GeTuiConfig getui) throws IOException {
	 
		IGtPush push = new IGtPush(getui.getHost(), getui.getAppKey(), getui.getMaster());
	    push.connect();
	    String tranContent = getui.getContent();
	    Transmission transmission = getui.getTransmission();
	    if(transmission != null){
	    	tranContent = transmission.toJSON();
	    }
	    TransmissionTemplate template = PushTemplate.transmissionTemplate(getui.getAppId(), getui.getAppKey(), getui.getTitle(), getui.getContent(), tranContent);
	    SingleMessage message = new SingleMessage(); 
	    message.setOffline(true);
	    message.setOfflineExpireTime(OFFLINE_EXPIRE_TIME);
	    message.setPushNetWorkType(0);
	    message.setData(template);
	    Target target = new Target();
	    target.setAppId(getui.getAppId());
        target.setClientId(clientId);
	    IPushResult ret;
        try {
            ret = push.pushMessageToSingle(message, target);
        } catch (RequestException e) {
            e.printStackTrace();
            ret = push.pushMessageToSingle(message, target, e.getRequestId());
        }
        return ret;
	}
}
