package com.yoogurt.taxi.common.helper.excel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExcelHeader {
    /**  */
    private static final long serialVersionUID = -6123981442217406533L;

    /**
     * 列名对应的英文字段
     * key:数据库Do或者model的字段属性
     */
    private String            columnKey;

    /**
     * excel头部列名
     */
    private String            columnName;

    /** 顺序值,顺序值越低排在越前 */
    private int               order;
}
