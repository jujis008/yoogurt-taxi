package com.yoogurt.taxi.common.helper.excel;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrorCellBean {
    private String errorMessage;
    private String cellValue;
    private Integer rowIndex;
    private Integer colIndex;

    public ErrorCellBean(String errorMessage, String cellValue, Integer rowIndex, Integer colIndex) {
        this.errorMessage = errorMessage;
        this.cellValue = cellValue;
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
    }
}
