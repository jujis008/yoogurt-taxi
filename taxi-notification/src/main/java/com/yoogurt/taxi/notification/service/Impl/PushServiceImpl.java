package com.yoogurt.taxi.notification.service.Impl;

import com.gexin.rp.sdk.base.IPushResult;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.Message;
import com.yoogurt.taxi.dal.beans.PushDevice;
import com.yoogurt.taxi.dal.enums.*;
import com.yoogurt.taxi.notification.bo.Transmission;
import com.yoogurt.taxi.notification.bo.TransmissionPayload;
import com.yoogurt.taxi.notification.config.getui.IGeTuiConfig;
import com.yoogurt.taxi.notification.dao.PushDeviceDao;
import com.yoogurt.taxi.notification.factory.GeTuiFactory;
import com.yoogurt.taxi.notification.form.UserBindingForm;
import com.yoogurt.taxi.notification.helper.PushHelper;
import com.yoogurt.taxi.notification.service.MessageService;
import com.yoogurt.taxi.notification.service.PushService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PushServiceImpl implements PushService {

    @Autowired
    private PushHelper pushHelper;

    @Autowired
    private PushDeviceDao deviceDao;

    @Autowired
    private MessageService messageService;

    @Override
    public PushDevice getDeviceInfo(String clientId, Long userId) {
        if (StringUtils.isBlank(clientId)) return null;
        PushDevice device = deviceDao.selectById(clientId);
        if (device == null) return null;
        if (userId != null && !userId.equals(device.getUserId())) return null;
        return device;
    }

    @Override
    public PushDevice getDeviceByUserId(Long userId) {
        PushDevice probe = new PushDevice();
        probe.setUserId(userId);
        List<PushDevice> devices = deviceDao.selectList(probe);
        if (CollectionUtils.isEmpty(devices)) return null;
        return devices.get(0);
    }

    /**
     * <p class="detail">
     * 功能：用户与设备绑定，兼容未注册设备
     * </p>
     *
     * @param bindingForm 绑定信息表单
     * @return 设备信息
     * @author weihao.liu
     */
    @Override
    public PushDevice binding(UserBindingForm bindingForm) {
        if (bindingForm == null) return null;
        String clientId = bindingForm.getClientId();
        Long userId = bindingForm.getUserId();
        //查看该用户是否绑定了设备
        PushDevice deviceInfo = getDeviceByUserId(userId);
        PushDevice target = getDeviceInfo(clientId, null);//需要绑定的目标设备
        if (deviceInfo == null) {    //该用户未绑定设备
            if (target != null) {//传入的设备已被绑定
                if (!userId.equals(target.getUserId())) {//但是绑定的不是传入的用户
                    target.setUserId(userId);
                    return saveDevice(target, false);//将此设备切换到传入的用户
                }
            } else {//传入的设备未绑定，且该用户也未绑定其他设备，则可以新生成一个设备绑定记录
                deviceInfo = new PushDevice(clientId);//新生成一个设备绑定记录
                BeanUtils.copyProperties(bindingForm, deviceInfo);
                deviceInfo.setStatus(DeviceStatus.BIND.getStatus());
                return saveDevice(deviceInfo, true);
            }
        } else {
            if (!clientId.equals(deviceInfo.getClientId())) {//绑定的不是传入的设备
                deviceInfo.setUserId(null);
                deviceInfo.setStatus(DeviceStatus.UNBIND.getStatus());
                saveDevice(deviceInfo, false);//将原来绑定的设备解绑
                if (target != null) {//新传入的设备已被绑定
                    target.setUserId(userId);
                    return saveDevice(target, false);//将此设备切换到传入的用户
                } else {
                    deviceInfo = new PushDevice();//新生成一个设备绑定记录
                    BeanUtils.copyProperties(bindingForm, deviceInfo);
                    deviceInfo.setStatus(DeviceStatus.BIND.getStatus());
                    return saveDevice(deviceInfo, true);
                }
            }
        }
        return deviceInfo;
    }

    @Override
    public PushDevice saveDevice(PushDevice device, boolean isAdd) {
        if (device == null) return null;
        if (isAdd) {
            return deviceDao.insertSelective(device) == 1 ? device : null;
        } else {
            return deviceDao.updateById(device) == 1 ? device : null;
        }
    }

    /**
     * 用户推送设备解绑
     *
     * @param clientId 设备ID
     * @param userId   用户ID
     * @return 设备信息
     */
    @Override
    public PushDevice unBinding(String clientId, Long userId) {
        PushDevice device = getDeviceInfo(clientId, userId);
        if (device == null) return null;
        device.setUserId(null);
        device.setStatus(DeviceStatus.UNBIND.getStatus());
        return saveDevice(device, false);
    }

    /**
     * <p class="detail">
     * 功能：群推消息，含ANDROID和iOS设备，需要设置推送内容，仅支持普通消息。
     * </p>
     *
     * @param userType 用户类型，必填项
     * @param title    消息标题
     * @param content  推送内容
     * @param persist  是否将推送消息持久化到数据库，可以在消息中心里面看到
     * @return 推送结果
     * @author weihao.liu
     */
    @Override
    public ResponseObj pushMessage(UserType userType, String title, String content, boolean persist) {

        ResponseObj pushResult = pushMessage(null, userType, SendType.COMMON, MsgType.ALL, DeviceType.ALL, title, content, null, persist);
        log.info(pushResult.toJSON());
        return pushResult;
    }

    /**
     * <p class="detail">
     * 功能：群推消息，指定设备类型，需要设置推送内容，仅支持普通消息。
     * </p>
     *
     * @param userType   用户类型，必填项
     * @param deviceType 设备类型
     * @param title      消息标题
     * @param content    推送内容
     * @param persist    是否持久化到数据库，可以在消息中心里面看到
     * @return 推送结果
     * @author weihao.liu
     */
    @Override
    public ResponseObj pushMessage(UserType userType, DeviceType deviceType, String title, String content, boolean persist) {
        ResponseObj pushResult = pushMessage(null, userType, SendType.COMMON, MsgType.ALL, deviceType, title, content, null, persist);
        log.info(pushResult.toJSON());
        return pushResult;
    }

    /**
     * <p class="detail">
     * 功能：单个推送消息，指定用户和消息发送类型
     * </p>
     *
     * @param userId   推送的用户id
     * @param userType 用户类型，必填项
     * @param sendType 推送类型 {@link SendType}
     * @param title    消息标题
     * @param content  推送通知内容
     * @param extras   额外的参数，用于客户端后台逻辑处理
     * @param persist  是否持久化到数据库，可以在消息中心里面看到
     * @return 推送结果
     * @author weihao.liu
     */
    @Override
    public ResponseObj pushMessage(Long userId, UserType userType, SendType sendType, String title, String content, Map<String, Object> extras, boolean persist) {
        List<Long> userIds = new ArrayList<>();
        userIds.add(userId);
        ResponseObj pushResult = pushMessage(userIds, userType, sendType, MsgType.SINGLE, null, title, content, extras, persist);
        log.info("userId: [" + userId + "]");
        log.info(pushResult.toJSON());
        return pushResult;
    }

    /**
     * <p class="detail">
     * 功能：批量推送给指定的用户群
     * </p>
     *
     * @param userIds  多个用户id
     * @param userType 用户类型
     * @param sendType 发送类型
     * @param title    消息标题
     * @param content  发送内容
     * @param extras   额外信息
     * @param persist  是否持久化到数据库，可以在消息中心里面看到
     * @return 推送结果
     * @author weihao.liu
     */
    @Override
    public ResponseObj pushMessage(List<Long> userIds, UserType userType, SendType sendType, String title, String content, Map<String, Object> extras, boolean persist) {
        ResponseObj pushResult = pushMessage(userIds, userType, sendType, MsgType.SINGLE, null, title, content, extras, persist);
        log.info("userIds: " + userIds);
        log.info(pushResult.toJSON());
        return pushResult;
    }


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
    @Override
    public ResponseObj pushMessage(List<Long> userIds, UserType userType, SendType sendType, MsgType msgType, DeviceType deviceType, String title, String content, Map<String, Object> extras, boolean persist) {
        if (msgType == null) {

            return ResponseObj.fail(StatusCode.BIZ_FAILED, "不支持的消息类型");

        } else if (MsgType.SINGLE.equals(msgType) && CollectionUtils.isEmpty(userIds)) {//单个推送消息，未指定推送对象

            return ResponseObj.fail(StatusCode.BIZ_FAILED, "请指定消息推送对象");
        }
        if (MsgType.ALL.equals(msgType) && deviceType == null) {//群推消息，未指定目标设备类型

            return ResponseObj.fail(StatusCode.BIZ_FAILED, "不支持的设备类型");
        }
        if (sendType == null) {

            return ResponseObj.fail(StatusCode.BIZ_FAILED, "不支持的发送类型");
        }
        if (userType == null) {

            return ResponseObj.fail(StatusCode.BIZ_FAILED, "未指定应用类型");
        }
        if (StringUtils.isBlank(content)) {

            return ResponseObj.fail(StatusCode.BIZ_FAILED, "请指定消息内容");
        }

        try {
            IGeTuiConfig config = GeTuiFactory.getConfigFactory(userType).generateConfig();
            Transmission transmission = Transmission.builder().sendType(sendType).title(title).content(content).extras(extras).build();
            TransmissionPayload payload = new TransmissionPayload(config, transmission);
            log.info("PUSH: " + transmission.toJSON());
            IPushResult pushResult = null;
            if(CollectionUtils.isNotEmpty(userIds)) {
                String clientId;
                if (userIds.size() == 1) {//如果只有一个用户，则退化成单推
                    PushDevice device = getDeviceByUserId(userIds.get(0));
                    if (device == null) {

                        return ResponseObj.fail(StatusCode.BIZ_FAILED, "用户未绑定设备");
                    }
                    clientId = device.getClientId();
                    pushResult = pushHelper.push(msgType, deviceType, clientId, payload);

                } else if (userIds.size() > 1) {//指定用户群
                    List<PushDevice> devices = getDeviceByUserIds(userIds);
                    if (devices != null && devices.size() > 0) {
                        List<String> clientIds = new ArrayList<>();
                        for (PushDevice device : devices) {
                            clientIds.add(device.getClientId());
                        }
                        pushResult = pushHelper.push(clientIds, payload);
                    } else {

                        return ResponseObj.fail(StatusCode.BIZ_FAILED, "用户未绑定设备");
                    }
                }
            } else {
                pushResult = pushHelper.push(msgType, deviceType, "", payload);
            }
            if (pushResult == null) return ResponseObj.fail(StatusCode.SYS_ERROR, "推送消息失败");

            Map<String, Object> response = pushResult.getResponse();
            log.info("Message push result: " + response.toString());
            if (!response.get("result").toString().equals("ok")) {

                return ResponseObj.fail(StatusCode.BIZ_FAILED, "推送消息失败");
            }
            if (persist) {//持久化到数据库
                persistMessage(userIds, title, content, sendType.getCode());
            }
        } catch (Exception e) {
            log.error("Message push failed: {}", e);
            return ResponseObj.fail(StatusCode.SYS_ERROR, "推送消息失败");
        }
        return ResponseObj.success();
    }

    private boolean persistMessage(List<Long> userIds, String title, String content, Integer type) {
        if (CollectionUtils.isEmpty(userIds)) return false;
        List<Message> messages = new ArrayList<>();
        for (Long userId : userIds) {
            Message msg = new Message();
            msg.setToUserId(userId);
            msg.setTitle(title);
            msg.setContent(content);
            msg.setType(type);
            msg.setStatus(10);
            messages.add(msg);
        }
        return messageService.addMessages(messages) == userIds.size();
    }

    private List<PushDevice> getDeviceByUserIds(List<Long> userIds) {

        Example ex = new Example(PushDevice.class);
        ex.createCriteria().andIn("userId", userIds);
        return deviceDao.selectByExample(ex);
    }
}
