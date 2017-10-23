package com.yoogurt.taxi.common.helper.excel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExcelData {

    /**  */
    private static final long         serialVersionUID = 1822797079552507763L;

    /** excel的表名 */
    private String                    sheetName;

    /**
     * 要导出的列名
     */
    private List<ExcelHeader> excelHeaders;

    /**
     * 要导出的实体：List代表所有行
     * Map<String, String> 内部为每行每列的key-value
     * key:数据库Do或者model的字段属性columnKey，value 为其属性对应的值
     */
    private List<Map<String, Object>> bodyDatas;
}
