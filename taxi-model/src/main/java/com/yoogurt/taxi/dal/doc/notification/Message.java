package com.yoogurt.taxi.dal.doc.notification;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yoogurt.taxi.dal.enums.SendType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.Map;

@Setter
@Getter
public class Message {

    /**
     * 消息id
     */
    @Id
    private Long messageId;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 消息内容（不是富文本）
     */
    private String content;

    /**
     * 消息状态：10有效，20失效
     */
    private Integer status;

    /**
     * 消息类型：10系统通知  20订单消息  30违约信息  40违章信息  50提现信息
     */
    private SendType sendType;

    /**
     * 接收者userId（系统消息为空）
     */
    private Long toUserId;

    /**
     * 额外参数
     */
    private Map<String, Object> extras;

    /**
     * 创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date gmtCreate;

}