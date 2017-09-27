package com.yoogurt.taxi.common.ExcelHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ExcelUtils {

    public static Map<ExcelParamBean, List<CellPropertyBean>> importExcel(InputStream inputStream, List<ExcelParamBean> paramBeanList) throws IOException, InvalidFormatException, InvalidFormatException {
        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        int rows = sheet.getLastRowNum() + 1;
        Row tmp = sheet.getRow(0);
        if (tmp == null) {
            return null;
        }
        int cols = tmp.getPhysicalNumberOfCells();
        Map<ExcelParamBean, List<CellPropertyBean>> map = new HashMap<>();
        for (int col = 0; col < cols; col++) {
            List<CellPropertyBean> list = new ArrayList<>();
            for (ExcelParamBean paramBean : paramBeanList) {
                if (paramBean.getIndex() == col) {
                    for (int row = 1; row < rows; row++) {
                        Row r = sheet.getRow(row);
                        list.add(new CellPropertyBean(getCellValue(r.getCell(col)).toString(), (row + 1), (col + 1)));
                    }
                    map.put(paramBean, list);
                }
            }
        }
        return map;
    }

    public static List<Map<String, Object>> importExcel(InputStream inputStream, List<ExcelParamBean> paramBeanList, Set<Integer> errorSet) throws IOException, InvalidFormatException {
        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        int rows = sheet.getLastRowNum() + 1;
        Row tmp = sheet.getRow(0);
        if (tmp == null) {
            return null;
        }
        int cols = tmp.getPhysicalNumberOfCells();
        List<Map<String, Object>> rightList = new ArrayList<>();
        for (int row = 1; row < rows; row++) {
            if (errorSet.contains(row+1)) {
                continue;
            }
            Map<String, Object> map = new HashMap<>();
            for (int col = 0; col < cols; col++) {
                for (ExcelParamBean paramBean : paramBeanList) {
                    if (paramBean.getIndex() == col) {
                        Row r = sheet.getRow(row);
                        map.put(paramBean.getParam(), getCellValue(r.getCell(col)).toString());
                    }
                }
            }
            rightList.add(map);
        }
        return rightList;
    }

    public static Object getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        } else {
            CellType cellType = cell.getCellTypeEnum();
            if (cellType.equals(CellType.STRING)) {
                return StringUtils.trimToEmpty(cell.getStringCellValue());
            } else if (cellType.equals(CellType.NUMERIC)) {
                boolean b = DateUtil.isCellDateFormatted(cell);
                if (b) {
                    return com.yoogurt.taxi.common.utils.DateUtil.dateToStr(cell.getDateCellValue(),"yyyy-MM-dd HH:mm:ss");
                }
                return cell.getNumericCellValue();
            } else if (cellType.equals(CellType.FORMULA)) {
                return !StringUtils.isBlank(cell.getStringCellValue()) ? StringUtils.trimToEmpty(cell.getStringCellValue()) : cell.getNumericCellValue();
            } else if (!cellType.equals(CellType.BLANK) && !cellType.equals(CellType.ERROR)) {
                return cellType.equals(CellType.BOOLEAN) ? cell.getBooleanCellValue() : "";
            } else {
                return "";
            }
        }
    }

    public static List<ErrorCellBean> filter(Map<ExcelParamBean, List<CellPropertyBean>> map, Set<Integer> errorSet) {
        List<Map<String, Object>> rightList = new ArrayList<>();
        List<ErrorCellBean> errorCellList = new ArrayList<>();
        for (ExcelParamBean bean : map.keySet()) {
            String pattern = bean.getPattern();
            String errorMessage = bean.getErrorMessage();
            String repeatShowMessage = bean.getRepeatShowMessage();
            List<CellPropertyBean> dataList = map.get(bean);
            if (StringUtils.isNotBlank(pattern)) {
                List<CellPropertyBean> errorList = dataList.stream().filter(e -> !Pattern.matches(pattern, e.getCellValue())).collect(Collectors.toList());
                errorList.forEach(e -> errorSet.add(e.getRowIndex()));
                errorList.forEach(e -> errorCellList.add(new ErrorCellBean(errorMessage, e.getCellValue(), e.getRowIndex(),e.getColIndex())));
            }
            if (bean.isRepeatFlag()) {
                Map<String, Long> map1 = dataList.stream().collect(Collectors.groupingBy(CellPropertyBean::getCellValue, Collectors.counting()));
                for (String key : map1.keySet()) {
                    if (map1.get(key).intValue() > 1) {
                        dataList.stream().filter(e -> e.getCellValue().equals(key)).forEach(e -> errorCellList.add(new ErrorCellBean(repeatShowMessage, e.getCellValue(), e.getRowIndex(),e.getColIndex())));
                        dataList.stream().filter(e -> e.getCellValue().equals(key)).forEach(e -> errorSet.add(e.getRowIndex()));
                    }
                }
            }
        }
        return errorCellList;
    }

    public static void main(String[] args) throws IOException, InvalidFormatException {
        String filePath = "D:\\我的文档\\Documents\\Tencent Files\\1316549200\\FileRecv\\司机信息表.xls";
        File file = new File(filePath);
        InputStream inputStream = new FileInputStream(file);
        List<ExcelParamBean> paramBeanList = new ArrayList<>();
        ExcelParamBean bean1 = new ExcelParamBean(1, "username", "^[\\u4e00-\\u9fa5\\s]{2,8}$", "姓名长度在2-8个字符", Boolean.FALSE, null);
        ExcelParamBean bean2 = new ExcelParamBean(2, "idCard", "^(\\d{18}$|^\\d{17}(\\d|X|x))$", "身份证格式有误", Boolean.TRUE, "身份证号重复");
        ExcelParamBean bean3 = new ExcelParamBean(3, "birthday",null, null, Boolean.FALSE, "身份证号重复");
        paramBeanList.add(bean1);
        paramBeanList.add(bean2);
        paramBeanList.add(bean3);
        Map<ExcelParamBean, List<CellPropertyBean>> map = importExcel(inputStream, paramBeanList);
        Set<Integer> set = new HashSet<>();
        List<ErrorCellBean> errorCellBeanList = filter(map, set);
        List<Map<String, Object>> list = importExcel(inputStream, paramBeanList, set);
        System.out.println(set.size());
        System.out.println(list.size());
    }
}
