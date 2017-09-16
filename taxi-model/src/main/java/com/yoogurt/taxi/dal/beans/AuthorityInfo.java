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

}