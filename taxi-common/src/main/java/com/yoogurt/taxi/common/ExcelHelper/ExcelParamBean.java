package com.yoogurt.taxi.common.ExcelHelper;

public class ExcelParamBean {
    private int index;
    private String param;
    private String pattern;
    private String errorMessage;
    private boolean repeatFlag;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isRepeatFlag() {
        return repeatFlag;
    }

    public void setRepeatFlag(boolean repeatFlag) {
        this.repeatFlag = repeatFlag;
    }

    public String getRepeatShowMessage() {
        return repeatShowMessage;
    }

    public void setRepeatShowMessage(String repeatShowMessage) {
        this.repeatShowMessage = repeatShowMessage;
    }

    public ExcelParamBean(int index, String param, String pattern, String errorMessage, boolean repeatFlag, String repeatShowMessage) {

        this.index = index;
        this.param = param;
        this.pattern = pattern;
        this.errorMessage = errorMessage;
        this.repeatFlag = repeatFlag;
        this.repeatShowMessage = repeatShowMessage;
    }

    private String repeatShowMessage;

}
