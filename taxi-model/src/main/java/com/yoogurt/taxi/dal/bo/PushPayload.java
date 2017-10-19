package com.yoogurt.taxi.dal.bo;

import com.yoogurt.taxi.dal.enums.DeviceType;
import com.yoogurt.taxi.dal.enums.MsgType;
import com.yoogurt.taxi.dal.enums.SendType;
import com.yoogurt.taxi.dal.enums.UserType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class PushPayload implements Serializable {

    private List<Long> userIds;

    private UserType userType;

    private MsgType msgType = MsgType.ALL;

    private DeviceType deviceType = DeviceType.ALL;

    private SendType sendType = SendType.COMMON;

    private String title;

    private String content;

    private Map<String, Object> extras;

    private boolean persist;

}
