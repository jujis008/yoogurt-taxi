package com.yoogurt.taxi.common.helper.excel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ExcelParamBean {
    private int index;
    private String param;
    private String pattern;
    private String errorMessage;
    private boolean repeatFlag;
    private String repeatShowMessage;

}
