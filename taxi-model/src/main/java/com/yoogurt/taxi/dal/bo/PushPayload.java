package com.yoogurt.taxi.dal.bo;

import com.yoogurt.taxi.dal.enums.DeviceType;
import com.yoogurt.taxi.dal.enums.MsgType;
import com.yoogurt.taxi.dal.enums.SendType;
import com.yoogurt.taxi.dal.enums.UserType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class PushPayload implements Serializable {

    private List<String> userIds;

    private UserType userType;

    private MsgType msgType = MsgType.ALL;

    private DeviceType deviceType = DeviceType.ALL;

    private SendType sendType = SendType.COMMON;

    private String title;

    private String content;

    private Map<String, Object> extras;

    private boolean persist = true;

    private PushPayload() {
        this.userIds = new ArrayList<>();
    }

    public PushPayload(UserType userType, SendType sendType) {
        this();
        this.userType = userType;
        this.sendType = sendType;
    }

    public PushPayload(UserType userType, SendType sendType, String title, String content) {
        this();
        if(sendType != null && StringUtils.isBlank(content)) {
            content = sendType.getMessage();
        }
        this.userType = userType;
        this.sendType = sendType;
        this.title = title;
        this.content = content;
    }

    public PushPayload(UserType userType, SendType sendType, String title) {
        this();
        this.userType = userType;
        this.sendType = sendType;
        this.title = title;
        if (sendType != null) {
            this.content = sendType.getMessage();
            if (sendType.equals(SendType.COMMON)) {
                this.msgType = MsgType.ALL;
                this.deviceType = DeviceType.ALL;
            } else {
                this.msgType = MsgType.SINGLE;
            }
        }
    }

    public void addUserId(String userId) {
        if (StringUtils.isNotBlank(userId)) {
            this.userIds.add(userId);
        }
    }
}
