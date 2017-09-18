package com.yoogurt.taxi.dal.common;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;
import java.util.Date;

@Getter
@Setter
@MappedSuperclass
public class SuperModel {

    /**
     * 是否删除
     */
    private Boolean isDeleted;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 创建人ID
     */
    private Long creator;

    /**
     * 最后修改时间
     */
    private Date gmtModify;

    /**
     * 最后修改人ID
     */
    private Long modifier;
}
