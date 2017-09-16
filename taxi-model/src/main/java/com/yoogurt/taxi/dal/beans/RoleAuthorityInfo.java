package com.yoogurt.taxi.dal.beans;

import com.yoogurt.taxi.dal.annotation.Domain;
import com.yoogurt.taxi.dal.common.SuperModel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Domain
@Getter
@Setter
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

}