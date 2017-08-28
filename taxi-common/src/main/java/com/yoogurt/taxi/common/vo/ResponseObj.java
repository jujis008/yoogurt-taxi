package com.yoogurt.taxi.common.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yoogurt.taxi.common.enums.StatusCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Description:
 * 客户端响应返回值封装。
 * @Author Eric Lau
 * @Date 2017/8/28.
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseObj {

    /**
     * 返回的状态码
     */
    private int status = StatusCode.INFO_SUCCESS.getStatus();

    /**
     * 提示信息
     */
    private String message = StatusCode.INFO_SUCCESS.getDetail();

    /**
     * 返回的数据主体内容
     */
    private Object body;

    /**
     * 返回的额外信息，无特殊情况，不使用
     */
    private Map<String, Object> extras;
}
