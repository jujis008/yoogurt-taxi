package com.yoogurt.taxi.dal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SmsTemplateType {
    /**
     *验证码
     */
    VALID(1,"验证码"),
    /**
     * 车主端短信密码通知
     */
    OFFICE_PWD(2,"车主端短信密码通知"),
    /**
     * 司机端短信密码通知
     */
    AGENT_PWD(3,"司机端短信密码通知"),
    /**
     * 车主短信重置密码通知
     */
    OFFICE_RESET_PWD(4,"车主短信重置密码通知"),
    /**
     * 司机短信重置密码通知
     */
    AGENT_RESET_PWD(4,"司机短信重置密码通知"),
    ;

    private Integer code;
    private String name;

    public static SmsTemplateType getEnumsByCode(Integer code) {
        for (SmsTemplateType enums:SmsTemplateType.values()) {
            if (enums.code.equals(code)) {
                return enums;
            }
        }
        return null;
    }
}
