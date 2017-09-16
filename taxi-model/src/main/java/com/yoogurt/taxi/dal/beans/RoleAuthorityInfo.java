package com.yoogurt.taxi.dal.beans;

import com.yoogurt.taxi.dal.common.SuperModel;

import javax.persistence.*;
import java.util.Date;

@Table(name = "role_authority_info")
public class RoleAuthorityInfo extends SuperModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 角色id（role_info）
     */
    @Column(name = "role_id")
    private Long roleId;

    /**
     * 权限id
     */
    @Column(name = "authority_id")
    private Long authorityId;


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
     * 获取角色id（role_info）
     *
     * @return role_id - 角色id（role_info）
     */
    public Long getRoleId() {
        return roleId;
    }

    /**
     * 设置角色id（role_info）
     *
     * @param roleId 角色id（role_info）
     */
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    /**
     * 获取权限id
     *
     * @return authority_id - 权限id
     */
    public Long getAuthorityId() {
        return authorityId;
    }

    /**
     * 设置权限id
     *
     * @param authorityId 权限id
     */
    public void setAuthorityId(Long authorityId) {
        this.authorityId = authorityId;
    }

}