package com.yoogurt.taxi.notification.service;

import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.PushDevice;
import com.yoogurt.taxi.dal.enums.SendType;
import com.yoogurt.taxi.dal.enums.UserType;
import com.yoogurt.taxi.notification.form.UserBindingForm;

import java.util.List;
import java.util.Map;

public interface PushService {

    PushDevice getDeviceByUserId(Long userId);

    PushDevice getDeviceInfo(String clientId, Long userId);

    PushDevice saveDevice(PushDevice device, boolean isAdd);

    /**
     * 用户绑定设备
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
    PushDevice unBinding(String clientId, Long userId);

    /**
     * <p class="detail">
     * 功能：群推消息，含ANDROID和iOS设备，需要设置推送内容，仅支持普通消息。
     * </p>
     *
     *
     * @param userType
     * @param title
     *@param content 推送内容
     * @param persist 是否将推送消息持久化到数据库，可以在消息中心里面看到   @return 推送结果
     * @author weihao.liu
     */
    ResponseObj pushMessage(UserType userType, String title, String content, boolean persist);


    /**
     * <p class="detail">
     * 功能：群推消息，指定设备类型，需要设置推送内容，仅支持普通消息。
     * </p>
     *
     *
     * @param userType   用户类型，必填项
     * @param deviceType 设备类型
     * @param title      标题
     * @param content    推送内容
     * @param persist    是否持久化到数据库，可以在消息中心里面看到   @return 推送结果
     * @author weihao.liu
     */
    ResponseObj pushMessage(UserType userType, String deviceType, String title, String content, boolean persist);

    /**
     * <p class="detail">
     * 功能：单个推送消息，指定用户和消息发送类型
     * </p>
     *  @param userId   推送的用户id
     * @param userType
     * @param sendType 推送类型 {@link SendType}
     * @param title
     * @param content  推送通知内容
     * @param extras   额外的参数，用于客户端后台逻辑处理
     * @param persist  是否持久化到数据库，可以在消息中心里面看到     @return 推送结果
     * @author weihao.liu
     * @date 2016年12月31日
     */
    ResponseObj pushMessage(Long userId, UserType userType, String sendType, String title, String content, Map<String, Object> extras, boolean persist);

    /**
     * <p class="detail">
     * 功能：批量推送给指定的用户群
     * </p>
     *  @param userIds  多个用户id
     * @param userType
     * @param sendType 发送类型
     * @param title
     * @param content  发送内容
     * @param extras   额外信息
     * @param persist  是否持久化到数据库，可以在消息中心里面看到     @return 推送结果
     * @author weihao.liu
     */
    ResponseObj pushMessage(List<Long> userIds, UserType userType, String sendType, String title, String content, Map<String, Object> extras, boolean persist);

}
