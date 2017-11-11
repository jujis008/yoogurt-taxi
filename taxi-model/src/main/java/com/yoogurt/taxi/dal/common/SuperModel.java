package com.yoogurt.taxi.dal.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

/**
 * 超级model，所有数据库的实体类都继承于此。
 * 没有特殊情况，Jackson序列化的时候，将会
 * 忽略isDeleted，creator和modifier三个
 * 字段(增加了@JsonIgnore注解)。
 * 如需返回三个字段，就不需要继承SuperModel，
 * 将公共字段直接放入数据库实体类中。
 */
@Getter
@Setter
@MappedSuperclass
public class SuperModel implements Serializable{

    /**
     * 是否删除
     */
    @Column(name = "is_deleted")
    @JsonIgnore
    private Boolean isDeleted;

    /**
     * 创建时间
     */
    @Column(name = "gmt_create")
    private Date gmtCreate;

    /**
     * 创建人ID
     */
    @Column(name = "creator")
    @JsonIgnore
    private String creator;

    /**
     * 最后修改时间
     */
    @Column(name = "gmt_modify")
    private Date gmtModify;

    /**
     * 最后修改人ID
     */
    @Column(name = "modifier")
    @JsonIgnore
    private String modifier;
}
