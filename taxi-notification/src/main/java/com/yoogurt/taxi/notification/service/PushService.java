package com.yoogurt.taxi.notification.service;

import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.PushDevice;
import com.yoogurt.taxi.dal.enums.DeviceType;
import com.yoogurt.taxi.dal.enums.MsgType;
import com.yoogurt.taxi.dal.enums.SendType;
import com.yoogurt.taxi.dal.enums.UserType;
import com.yoogurt.taxi.notification.form.UserBindingForm;

import java.util.List;
import java.util.Map;

public interface PushService {

    PushDevice getDeviceByUserId(String userId);

    PushDevice getDeviceInfo(String clientId, String userId);

    PushDevice saveDevice(PushDevice device, boolean isAdd);

    /**
     * 用户绑定设备
     *
     * @param bindingForm 设备ID
     * @return 设备信息
     */
    PushDevice binding(UserBindingForm bindingForm);

    /**
     * 用户推送设备解绑
     *
     * @param clientId 设备ID
     * @param userId   用户ID
     * @return 设备信息
     */
    PushDevice unBinding(String clientId, String userId);

    /**
     * <p class="detail">
     * 功能：群推消息，含ANDROID和iOS设备，需要设置推送内容，仅支持普通消息。
     * </p>
     *  @param userType 用户类型，必填项
     * @param title    标题
     * @param content  推送内容
     * @param persist  是否将推送消息持久化到数据库，可以在消息中心里面看到  @return 推送结果
     * @author weihao.liu
     */
    ResponseObj pushMessage(UserType userType, String title, String content, boolean persist);


    /**
     * <p class="detail">
     * 功能：群推消息，指定设备类型，需要设置推送内容，仅支持普通消息。
     * </p>
     *  @param userType   用户类型，必填项
     * @param deviceType 设备类型
     * @param title      标题
     * @param content    推送内容
     * @param persist    是否持久化到数据库，可以在消息中心里面看到  @return 推送结果
     * @author weihao.liu
     */
    ResponseObj pushMessage(UserType userType, DeviceType deviceType, String title, String content, boolean persist);

    /**
     * <p class="detail">
     * 功能：单个推送消息，指定用户和消息发送类型
     * </p>
     *  @param userId   推送的用户id
     * @param userType 用户类型，必填项
     * @param sendType 推送类型 {@link SendType}
     * @param title    标题
     * @param content  推送通知内容
     * @param extras   额外的参数，用于客户端后台逻辑处理
     * @param persist  是否持久化到数据库，可以在消息中心里面看到   @return 推送结果
     * @author weihao.liu
     */
    ResponseObj pushMessage(String userId, UserType userType, SendType sendType, String title, String content, Map<String, Object> extras, boolean persist);

    /**
     * <p class="detail">
     * 功能：批量推送给指定的用户群
     * </p>
     *  @param userIds  多个用户id
     * @param userType 用户类型，必填项
     * @param sendType 发送类型
     * @param title    标题
     * @param content  发送内容
     * @param extras   额外信息
     * @param persist  是否持久化到数据库，可以在消息中心里面看到   @return 推送结果
     * @author weihao.liu
     */
    ResponseObj pushMessage(List<String> userIds, UserType userType, SendType sendType, String title, String content, Map<String, Object> extras, boolean persist);

    /**
     * <p class="detail">
     * 功能：消息推送，含单个推和群推，不建议直接使用此方法。
     * </p>
     *
     * @param userIds    单个推送的时候，指定用户id，即为推送对象（兼容多个用户推送），如果用户未绑定设备，将导致推送失败。
     * @param userType   推送对象的用户类型，因为无法跨应用推送，所以push_all的情况下，只能在外部调用多次推送接口，分别传不同的userType
     * @param sendType   消息发送类型
     * @param msgType    消息类型
     * @param deviceType 目标设备类型，群推消息使用此参数
     * @param title      消息标题
     * @param content    消息体，可以直接显示在APP上
     * @param extras     透传内容，用于APP在后台进行逻辑处理，不直接显示给用户
     * @param persist    推送消息是否持久化到数据库
     * @return 推送结果
     * @author weihao.liu
     */
    ResponseObj pushMessage(List<String> userIds, UserType userType, SendType sendType, MsgType msgType, DeviceType deviceType, String title, String content, Map<String, Object> extras, boolean persist);

}
