package com.yoogurt.taxi.dal.beans;

import com.yoogurt.taxi.dal.annotation.Domain;
import com.yoogurt.taxi.dal.common.SuperModel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Domain
@Getter
@Setter
@Table(name = "comment_tag")
public class CommentTag extends SuperModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tag_name")
    private String tagName;

    @Column(name = "bg_color")
    private String bgColor;

    @Column(name = "font_color")
    private String fontColor;

    /**
     * 评价标签含义是否积极正面
     */
    private Boolean positive;

    /**
     * 标签类型：10-通用，20-司机端，30-车主端
     */
    private Integer type;

}