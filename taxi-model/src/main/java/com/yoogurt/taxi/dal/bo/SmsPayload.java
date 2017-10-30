package com.yoogurt.taxi.dal.bo;

import com.yoogurt.taxi.dal.enums.SmsTemplateType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
public class SmsPayload implements Serializable{

    private List<String> phoneNumbers;

    private String param;

    private SmsTemplateType type;

    public void addOne(String phoneNumber) {
        if (StringUtils.isNotBlank(phoneNumber)) {
            this.phoneNumbers.add(phoneNumber);
        }
    }
}
