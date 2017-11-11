package com.yoogurt.taxi.dal.beans;

import com.yoogurt.taxi.dal.annotation.Domain;
import com.yoogurt.taxi.dal.common.SuperModel;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import javax.persistence.*;

@Domain
@Setter
@Getter
@Table(name = "message_remove")
public class MessageRemove extends SuperModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "message_id")
    private Long messageId;

    /**
     * 是否删除：1-是，0-否
     */
    @Column(name = "is_deleted")
    private Boolean isDeleted;

}