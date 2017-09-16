package com.yoogurt.taxi.dal.common;

import javax.persistence.MappedSuperclass;
import java.util.Date;

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

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Long getCreator() {
        return creator;
    }

    public void setCreator(Long creator) {
        this.creator = creator;
    }

    public Date getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(Date gmtModify) {
        this.gmtModify = gmtModify;
    }

    public Long getModifier() {
        return modifier;
    }

    public void setModifier(Long modifier) {
        this.modifier = modifier;
    }
}
