package com.yoogurt.taxi.dal.model.account;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yoogurt.taxi.dal.enums.BillStatus;
import com.yoogurt.taxi.dal.enums.TradeType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
public class FinanceBillListModel {
    private Long id;
    private Long billNo;
    private Long contextId;
    private BigDecimal amount;
    private Integer type;
    private String typeName;
    private Integer status;
    private String statusName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date gmtCreate;

    public void setType(Integer type) {
        this.type = type;
        this.typeName = TradeType.getEnumsBycode(type).getName();
    }

    public void setStatus(Integer status) {
        this.status = status;
        this.statusName = BillStatus.getEnumsByCode(status).getName();
    }
}
