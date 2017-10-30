package com.yoogurt.taxi.common.helper.excel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorCellBean {
    private String errorMessage;
    private String cellValue;
    private Integer rowIndex;
    private Integer colIndex;
}
