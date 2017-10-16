package com.yoogurt.taxi.notification.bo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yoogurt.taxi.dal.enums.SendType;
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
public class Transmission {

	private SendType sendType;
	
	private String content;
	
	private Map<String, Object> extras;

	public Transmission() { }

	public Transmission(SendType sendType, String content) {
		this.sendType = sendType;
		this.content = content;
	}

	public Transmission(SendType sendType, String content, Map<String, Object> extras) {
		this.sendType = sendType;
		this.content = content;
		this.extras = extras;
	}

	@Override
	public String toString() {
		return "Transmission [sendType=" + sendType + ", content=" + content + ", extras=" + extras + "]";
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
