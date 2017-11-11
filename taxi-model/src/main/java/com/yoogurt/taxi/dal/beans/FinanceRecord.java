package com.yoogurt.taxi.dal.beans;

import com.yoogurt.taxi.dal.annotation.Domain;
import com.yoogurt.taxi.dal.common.SuperModel;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import javax.persistence.*;

@Table(name = "finance_record")
@Domain
@Getter
@Setter
public class FinanceRecord extends SuperModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 账单id
     */
    @Column(name = "bill_id")
    private Long billId;

    /**
     * 账单号
     */
    @Column(name = "bill_no")
    private String billNo;

    /**
     * 交易状态：10-待处理，20-处理中，30-处理成功，40-失败，50拒绝
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

}