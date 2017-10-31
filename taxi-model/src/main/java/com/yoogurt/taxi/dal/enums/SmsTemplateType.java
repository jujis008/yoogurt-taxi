package com.yoogurt.taxi.dal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SmsTemplateType {
    VALID(1,"验证码"),
    office_pwd(2,"车主端短信密码通知"),
    agent_pwd(3,"司机端短信密码通知")
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
