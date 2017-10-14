package com.yoogurt.taxi.common.helper.excel;

public class CellBean {
    private String cellValue;
    private Integer cellRowIndex;
    private Integer cellColIndex;

    public CellBean(String cellValue, Integer cellRowIndex, Integer cellColIndex) {
        this.cellValue = cellValue;
        this.cellRowIndex = cellRowIndex;
        this.cellColIndex = cellColIndex;
    }

    public String getCellValue() {
        return cellValue;
    }

    public void setCellValue(String cellValue) {
        this.cellValue = cellValue;
    }

    public Integer getCellRowIndex() {
        return cellRowIndex;
    }

    public void setCellRowIndex(Integer cellRowIndex) {
        this.cellRowIndex = cellRowIndex;
    }

    public Integer getCellColIndex() {
        return cellColIndex;
    }

    public void setCellColIndex(Integer cellColIndex) {
        this.cellColIndex = cellColIndex;
    }

    @Override
    public String toString() {

        return this.cellValue+"="+"{"+this.cellRowIndex+","+this.cellColIndex+"}";
    }
}
