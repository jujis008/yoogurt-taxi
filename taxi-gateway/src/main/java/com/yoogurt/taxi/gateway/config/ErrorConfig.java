package com.yoogurt.taxi.gateway.config;

import com.yoogurt.taxi.common.enums.StatusCode;
import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;

import java.util.HashMap;
import java.util.Map;

@Component
public class ErrorConfig extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(RequestAttributes requestAttributes, boolean includeStackTrace) {

        Map<String, Object> attributes = super.getErrorAttributes(requestAttributes, includeStackTrace);

        return new HashMap<String, Object>(4) {{
            put("status", attributes.get("status"));
            put("message", StatusCode.SYS_ERROR.getDetail());
            put("extras", attributes);
        }};
    }
}
