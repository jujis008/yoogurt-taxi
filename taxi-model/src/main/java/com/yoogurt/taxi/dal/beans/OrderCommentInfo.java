package com.yoogurt.taxi.dal.beans;

import com.yoogurt.taxi.dal.annotation.Domain;
import com.yoogurt.taxi.dal.common.SuperModel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Domain
@Getter
@Setter
@Table(name = "order_comment_info")
public class OrderCommentInfo extends SuperModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 订单号
     */
    @Column(name = "order_id")
    private String orderId;

    /**
     * 发起评价的用户ID
     */
    @Column(name = "from_user_id")
    private String fromUserId;

    /**
     * 被评价的用户的ID
     */
    @Column(name = "to_user_id")
    private String toUserId;

    /**
     * 评分，整数，1-5
     */
    private Integer score;

    /**
     * 多个评价标签用逗号分隔
     */
    @Column(name = "tag_id")
    private String tagId;

    /**
     * 多个评价标签用逗号分隔
     */
    @Column(name = "tag_name")
    private String tagName;

    private String remark;

}