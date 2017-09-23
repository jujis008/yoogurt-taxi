package com.yoogurt.taxi.common.ExcelHelper;

public class CellPropertyBean {
    private String cellValue;
    private int rowIndex;
    private int colIndex;

    public CellPropertyBean(String cellValue, int rowIndex, int colIndex) {
        this.cellValue = cellValue;
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
    }

    public String getCellValue() {
        return cellValue;
    }

    public void setCellValue(String cellValue) {
        this.cellValue = cellValue;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public int getColIndex() {
        return colIndex;
    }

    public void setColIndex(int colIndex) {
        this.colIndex = colIndex;
    }
}
