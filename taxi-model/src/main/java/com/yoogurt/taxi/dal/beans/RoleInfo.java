package com.yoogurt.taxi.dal.beans;

import com.yoogurt.taxi.dal.common.SuperModel;

import javax.persistence.*;
import java.util.Date;

@Table(name = "role_info")
public class RoleInfo extends SuperModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 角色名称
     */
    @Column(name = "role_name")
    private String roleName;

    @Column(name = "role_code")
    private String roleCode;

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
     * 获取角色名称
     *
     * @return role_name - 角色名称
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * 设置角色名称
     *
     * @param roleName 角色名称
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    /**
     * @return role_code
     */
    public String getRoleCode() {
        return roleCode;
    }

    /**
     * @param roleCode
     */
    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

}