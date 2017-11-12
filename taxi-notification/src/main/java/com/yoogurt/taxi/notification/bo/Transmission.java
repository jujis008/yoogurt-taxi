package com.yoogurt.taxi.notification.bo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yoogurt.taxi.dal.enums.SendType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.util.Map;

/**
 * 透传消息体
 */
@Slf4j
@Getter
@Setter
@Builder
public class Transmission {

    /**
     * 消息发送类型
     */
    @Builder.Default
    private SendType sendType = SendType.COMMON;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 通知栏图标
     */
    @Builder.Default
    private String icon = "icon.png";

    /**
     * 额外的传输内容，便于App客户端处理
     */
    private Map<String, Object> extras;

    @Override
    public String toString() {
        return toJSON();
    }

    public String toJSON() {
        try {
            ObjectMapper m = new ObjectMapper();
            m.setDateFormat(DateFormat.getDateTimeInstance());
            return m.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.error("JSON序列化异常, {}", e);
        }
        return StringUtils.EMPTY;
    }
}
