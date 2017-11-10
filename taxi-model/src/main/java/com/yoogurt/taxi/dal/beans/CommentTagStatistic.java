package com.yoogurt.taxi.dal.beans;

import com.yoogurt.taxi.dal.annotation.Domain;
import com.yoogurt.taxi.dal.common.SuperModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Domain
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comment_tag_statistic")
public class CommentTagStatistic extends SuperModel {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "tag_id")
    private Long tagId;

    @Column(name = "tag_name")
    private String tagName;

    /**
     * 评价标签含义是否积极正面
     */
    private Boolean positive;

    /**
     * 标签类型：10-通用，20-司机端，30-车主端
     */
    private Integer type;

    private Integer counter;

    public CommentTagStatistic(Long userId) {
        this.userId = userId;
    }
}