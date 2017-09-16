package com.yoogurt.taxi.dal.beans;

import com.yoogurt.taxi.dal.common.SuperModel;

import javax.persistence.*;
import java.util.Date;

@Table(name = "authority_info")
public class AuthorityInfo extends SuperModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 权限名
     */
    @Column(name = "authority_name")
    private String authorityName;

    /**
     * 权限组名
     */
    @Column(name = "authority_group")
    private String authorityGroup;

    /**
     * 接口url
     */
    private String uri;

    /**
     * 关联控件
     */
    @Column(name = "associated_control")
    private String associatedControl;

    private String remark;


    /**
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取权限名
     *
     * @return authority_name - 权限名
     */
    public String getAuthorityName() {
        return authorityName;
    }

    /**
     * 设置权限名
     *
     * @param authorityName 权限名
     */
    public void setAuthorityName(String authorityName) {
        this.authorityName = authorityName;
    }

    /**
     * 获取权限组名
     *
     * @return authority_group - 权限组名
     */
    public String getAuthorityGroup() {
        return authorityGroup;
    }

    /**
     * 设置权限组名
     *
     * @param authorityGroup 权限组名
     */
    public void setAuthorityGroup(String authorityGroup) {
        this.authorityGroup = authorityGroup;
    }

    /**
     * 获取接口url
     *
     * @return uri - 接口url
     */
    public String getUri() {
        return uri;
    }

    /**
     * 设置接口url
     *
     * @param uri 接口url
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * 获取关联控件
     *
     * @return associated_control - 关联控件
     */
    public String getAssociatedControl() {
        return associatedControl;
    }

    /**
     * 设置关联控件
     *
     * @param associatedControl 关联控件
     */
    public void setAssociatedControl(String associatedControl) {
        this.associatedControl = associatedControl;
    }

    /**
     * @return remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

}