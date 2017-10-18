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
import com.yoogurt.taxi.notification.bo.TransmissionPayload;
import com.yoogurt.taxi.notification.bo.PushTemplate;
import com.yoogurt.taxi.notification.bo.Transmission;
import com.yoogurt.taxi.notification.config.IGeTuiConfig;
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
	 * @param msgType 消息类型
	 * @param deviceType 设备类型
	 * @param clientId 设备id，单推的时候不可为空
	 * @param payload
	 * @return
	 * @throws IOException
	 */
	public IPushResult push(MsgType msgType, DeviceType deviceType, String clientId, TransmissionPayload payload) throws IOException{
		if(msgType != null && msgType.equals(MsgType.SINGLE) && StringUtils.isNotBlank(clientId)){
			return singlePushByTransmission(clientId, payload);
		}else{
			switch (deviceType) {
				case ALL:
					return pushAll(payload);

				case ANDROID:
					return pushAllAndroid(payload);

				case IOS:
					return pushAllIOS(payload);

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
	 * @param payload
	 * @return
	 * @throws IOException
	 */
	public IPushResult push(List<String> clientIds, TransmissionPayload payload) throws IOException{
		IGeTuiConfig config = payload.getConfig();
		IGtPush push = getPush(payload.getConfig());
	    push.connect();
		Transmission transmission = payload.getTransmission();
		String tranContent = transmission.toJSON();
	    TransmissionTemplate template = PushTemplate.transmissionTemplate(config.getAppId(), config.getAppKey(), transmission.getTitle(), transmission.getContent(), tranContent);
	    ListMessage message = new ListMessage(); 
	    message.setOffline(true);
	    message.setOfflineExpireTime(OFFLINE_EXPIRE_TIME);
	    message.setPushNetWorkType(0);
	    message.setData(template);
	    List<Target> targets = new ArrayList<Target>();
	    for(String clientId:clientIds){
		    Target target = new Target();
		    target.setAppId(config.getAppId());
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
	 * @param payload
	 * @return
	 * @throws IOException
	 */
	public IPushResult pushAll(TransmissionPayload payload) throws IOException{
		IGeTuiConfig config = payload.getConfig();
		IGtPush push = getPush(config);
		//建立连接，开始鉴权
		push.connect();
		Transmission transmission = payload.getTransmission();
		String tranContent = transmission.toJSON();
		//透传模板
		TransmissionTemplate template = PushTemplate.transmissionTemplate(config.getAppId(), config.getAppKey(), transmission.getTitle(), transmission.getContent(), tranContent);
		AppMessage message = new AppMessage();
		message.setData(template);
		//设置消息离线，并设置离线时间
		message.setOffline(true);
        //离线有效时间，单位为毫秒，可选
		message.setOfflineExpireTime(OFFLINE_EXPIRE_TIME);
		//设置推送目标条件过滤
		List<String> appIdList = new ArrayList<>();
        appIdList.add(config.getAppId());
		message.setAppIdList(appIdList);
		return push.pushMessageToApp(message);
	}
	/**
	 * <p class="detail">
	 * 功能：向所有安卓设备推送通知消息
	 * </p>
	 * @author weihao.liu
	 * @date 2016年12月31日 
	 * @param payload
	 * @return
	 * @throws IOException
	 */
	public IPushResult pushAllAndroid(TransmissionPayload payload) throws IOException{
		IGeTuiConfig config = payload.getConfig();
		IGtPush push = getPush(config);
		//建立连接，开始鉴权
		push.connect();
		Transmission transmission = payload.getTransmission();
		String tranContent = transmission.toJSON();
		//透传模板
		TransmissionTemplate template = PushTemplate.transmissionTemplate(config.getAppId(), config.getAppKey(), transmission.getTitle(), transmission.getContent(), tranContent);
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
        appIdList.add(config.getAppId());
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
	 * @param payload
	 * @return
	 * @throws IOException
	 */
	public IPushResult pushAllIOS(TransmissionPayload payload) throws IOException {
		IGeTuiConfig config = payload.getConfig();
		IGtPush push = getPush(config);
		//建立连接，开始鉴权
		push.connect();
		Transmission transmission = payload.getTransmission();
		String tranContent = transmission.toJSON();
	    TransmissionTemplate template = PushTemplate.transmissionTemplate(config.getAppId(), config.getAppKey(), transmission.getTitle(), transmission.getContent(), tranContent);
	    AppMessage message = new AppMessage();
	    message.setOffline(true);
	    message.setOfflineExpireTime(OFFLINE_EXPIRE_TIME);
	    message.setPushNetWorkType(0);
	    
	    message.setData(template);
	    List<String> appIdList = new ArrayList<>();
	    appIdList.add(config.getAppId());
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
	  * @param payload
	  * @return
	  * @throws IOException
	  */
	public IPushResult singlePushByTransmission(String clientId, TransmissionPayload payload) throws IOException {

		IGeTuiConfig config = payload.getConfig();
		IGtPush push = getPush(config);
		//建立连接，开始鉴权
		push.connect();
		Transmission transmission = payload.getTransmission();
		String tranContent = transmission.toJSON();
		TransmissionTemplate template = PushTemplate.transmissionTemplate(config.getAppId(), config.getAppKey(), transmission.getTitle(), transmission.getContent(), tranContent);
	    SingleMessage message = new SingleMessage();
	    message.setOffline(true);
	    message.setOfflineExpireTime(OFFLINE_EXPIRE_TIME);
	    message.setPushNetWorkType(0);
	    message.setData(template);
	    Target target = new Target();
	    target.setAppId(config.getAppId());
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

	private IGtPush getPush(IGeTuiConfig config) {

		return new IGtPush(config.getHost(), config.getAppKey(), config.getMasterSecret());
	}

}
