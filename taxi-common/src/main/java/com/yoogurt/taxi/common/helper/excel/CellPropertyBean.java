package com.yoogurt.taxi.common.helper.excel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CellPropertyBean {
    private String cellValue;
    private int rowIndex;
    private int colIndex;
}
