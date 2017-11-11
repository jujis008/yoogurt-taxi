package com.yoogurt.taxi.dal.beans;

import com.yoogurt.taxi.dal.annotation.Domain;
import com.yoogurt.taxi.dal.common.SuperModel;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Map;
import javax.persistence.*;

@Domain
@Setter
@Getter
@Table(name = "message")
public class Message extends SuperModel {
    /**
     * 消息id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 消息内容（不是富文本）
     */
    private String content;

    @Column(name = "link_id")
    private String linkId;

    /**
     * 消息状态：10有效，20失效
     */
    private Integer status;

    /**
     * 消息类型：10系统通知  20订单消息  30违约信息  40违章信息  50提现信息
     */
    private Integer type;

    /**
     * 接收者userId（系统消息为空）
     */
    @Column(name = "to_user_id")
    private String toUserId;

}