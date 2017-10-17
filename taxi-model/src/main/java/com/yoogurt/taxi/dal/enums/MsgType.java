package com.yoogurt.taxi.dal.enums;

import org.apache.commons.lang.StringUtils;

/**
 * <p class="detail">
 * 功能：消息类型
    10-群推消息, 
    20-单个推送消息
 * </p>
 * @version V1.0
 * @author weihao.liu
 */
public enum MsgType {

    ALL("10", "群推"), // 含push_all类型的推送
    SINGLE("20", "单个推");

    private String  type;

    private String detail;

    MsgType(String type, String detail) {
        this.type = type;
        this.detail = detail;
    }

    public static MsgType getEnumByType(String type) {

        if (!StringUtils.isBlank(type)) {
            for (MsgType userType : MsgType.values()) {
                if (type.equals(userType.getType())) {
                    return userType;
                }
            }
        }
        return null;
    }

    public static String getDetailByType(String type) {

        if (!StringUtils.isBlank(type)) {
            for (MsgType userType : MsgType.values()) {
                if (type.equals(userType.getType())) {
                    return userType.getDetail();
                }
            }
        }
        return null;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

}
