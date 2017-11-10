package com.yoogurt.taxi.dal.beans;

import com.yoogurt.taxi.dal.annotation.Domain;
import com.yoogurt.taxi.dal.common.SuperModel;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import javax.persistence.*;

@Table(name = "access_record")
@Getter
@Setter
@Domain
public class AccessRecord extends SuperModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "link_id")
    private String linkId;

    @Column(name = "table_name")
    private String tableName;

    private String description;

    private String username;

    @Column(name = "user_type")
    private Integer userType;
}