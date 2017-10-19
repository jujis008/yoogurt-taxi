package com.yoogurt.taxi.dal.beans;

import com.yoogurt.taxi.dal.annotation.Domain;
import com.yoogurt.taxi.dal.common.SuperModel;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import javax.persistence.*;

@Getter
@Setter
@Table(name = "feedback_record")
@Domain
public class FeedbackRecord extends SuperModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    /**
     * 账号
     */
    private String username;

    @Column(name = "phone_model")
    private String phoneModel;
    @Column(name = "system_version")
    private String systemVersion;
    @Column(name = "app_type")
    private Integer appType;

    @Column(name = "app_version")
    private Integer appVersion;

    /**
     * 反馈id
     */
    @Column(name = "feedback_type")
    private Long feedbackType;

    /**
     * 反馈内容
     */
    @Column(name = "feedback_content")
    private String feedbackContent;

    /**
     * 是否处理
     */
    @Column(name = "is_handle")
    private Boolean isHandle;

}