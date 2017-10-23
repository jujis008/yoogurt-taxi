package com.yoogurt.taxi.common.helper.excel;

import com.yoogurt.taxi.common.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.util.CollectionUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class ExcelUtils {

    public static Map<ExcelParamBean, List<CellPropertyBean>> importExcel(InputStream inputStream, List<ExcelParamBean> paramBeanList) throws IOException, InvalidFormatException {
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
                        map.put(paramBean.getParam(), getCellValue(r.getCell(col)));
                    }
                }
            }
            rightList.add(map);
        }
        return rightList;
    }

    public static List<BankReceiptOfMerchantsModel> importExcelForWithdraw(InputStream inputStream) throws IOException, InvalidFormatException {
        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        int rows = sheet.getLastRowNum() + 1;
        Row tmp = sheet.getRow(0);
        if (tmp == null) {
            return null;
        }
//        int cols = tmp.getPhysicalNumberOfCells();
        List<BankReceiptOfMerchantsModel> list = new ArrayList<>();
        for (int row=1;row<rows;row++) {
            Row r = sheet.getRow(row);
            BankReceiptOfMerchantsModel model = new BankReceiptOfMerchantsModel();
            model.setAccountNo(getCellValue(r.getCell(0)).toString());
            model.setAccountName(getCellValue(r.getCell(1)).toString());
            model.setAmount(new BigDecimal(getCellValue(r.getCell(2)).toString()));
            model.setStatus(getCellValue(r.getCell(3)).equals("成功"));
            model.setId(getCellValue(r.getCell(4)).toString());
            model.setNote(getCellValue(r.getCell(5)).toString());
            model.setBankName(getCellValue(r.getCell(6)).toString());
            model.setBankAddress(getCellValue(r.getCell(7)).toString());
            list.add(model);
        }
        return list;
    }

    /**
     * 创建excel通用普通版
     *
     * @param excelDatas (可单个sheet(list只包含一个ExcelData);也可多个sheet(list包含多个ExcelData))
     * @return
     */
    public static Workbook createWorkBook(List<ExcelData> excelDatas) {

        if (CollectionUtils.isEmpty(excelDatas)) {
            log.error("创建excel数据参数为空");
            return null;
        }

        // 创建excel工作簿
        Workbook wb = new HSSFWorkbook();
        for (ExcelData excelData : excelDatas) {
            String sheetName = excelData.getSheetName();
            List<ExcelHeader> excelHeaders = excelData.getExcelHeaders();
            if (CollectionUtils.isEmpty(excelHeaders)) {
                log.error("创建excel数据列表参数为空");
                continue;
            }

            // 多少列
            int columns = excelHeaders.size();
            // 创建第一个sheet（页），并命名
            Sheet sheet = wb.createSheet(sheetName);
            // 手动设置列宽。第一个参数表示要为第几列设；，第二个参数表示列的宽度，n为列高的像素数。
            for (int i = 0; i < columns; i++) {
                sheet.setColumnWidth(i, (short) (35.7 * 150));
            }

            // 创建列名第一行
            Row headRow = sheet.createRow((short) 0);

            // 创建列名单元格格式
            CellStyle csHead = wb.createCellStyle();
            // 创建列名字体
            Font fHead = wb.createFont();
            // 创建第一种字体样式（用于列名）
            fHead.setFontHeightInPoints((short) 10);
            fHead.setColor(IndexedColors.BLACK.getIndex());
            fHead.setBold(Boolean.TRUE);

            // 设置第一种单元格的样式（用于列名）
            csHead.setFont(fHead);
            csHead.setBorderLeft(BorderStyle.THIN);
            csHead.setBorderRight(BorderStyle.THIN);
            csHead.setBorderBottom(BorderStyle.THIN);
            csHead.setBorderTop(BorderStyle.THIN);
            csHead.setAlignment(HorizontalAlignment.CENTER);

            //设置列名
            for (int i = 0; i < columns; i++) {
                Cell cell = headRow.createCell(i);
                cell.setCellValue(excelHeaders.get(i).getColumnName());
                cell.setCellStyle(csHead);
            }

            List<Map<String, Object>> bodyDatas = excelData.getBodyDatas();
            if (CollectionUtils.isEmpty(bodyDatas)) {
                log.error(sheetName + ": 该表要导出的数据为空");
                continue;
            }

            // 创建数据单元格格式
            CellStyle csBody = wb.createCellStyle();

            // 创建数据部分字体（用于值）
            Font fBody = wb.createFont();
            fBody.setFontHeightInPoints((short) 10);
            fBody.setColor(IndexedColors.BLACK.getIndex());

            // 设置第二种单元格的样式（用于值）
            csHead.setFont(fHead);
            csHead.setBorderLeft(BorderStyle.THIN);
            csHead.setBorderRight(BorderStyle.THIN);
            csHead.setBorderBottom(BorderStyle.THIN);
            csHead.setBorderTop(BorderStyle.THIN);
            csHead.setAlignment(HorizontalAlignment.CENTER);

            //设置每行每列的值
            for (int i = 0; i < bodyDatas.size(); i++) {
                // Row 行,Cell 方格 , Row 和 Cell 都是从0开始计数的
                // 创建一行，在页sheet上 第一行已被列名占用
                Row bodyRow = sheet.createRow(i + 1);
                // 排序看j值
                // 在row行上创建一个方格
                for (int j = 0; j < columns; j++) {
                    Cell cell = bodyRow.createCell(j);
                    String cellValue;
                    Object objCellValue = bodyDatas.get(i).get(excelHeaders.get(j).getColumnKey());
                    if (null == objCellValue) {
                        cellValue = "";
                    } else if (objCellValue instanceof java.util.Date) {
                        cellValue = DateUtils.dateToStr((Date) objCellValue,"yyyy-MM-dd HH:mm:ss");
                    } else if (objCellValue instanceof BigDecimal) {
                        cellValue = objCellValue.toString();
                    } else {
                        cellValue = String.valueOf(objCellValue);
                    }
                    cell.setCellValue(cellValue);
                    cell.setCellStyle(csBody);
                }
            }
        }
        return wb;
    }

    /**
     * 先创建excel，再下载excel
     * @param fileName   下载后的文件名
     * @param excelDatas 要导出的数据
     * @param response
     * @throws IOException
     */
    public static void createAndDownExcel(String fileName, List<ExcelData> excelDatas,
                                          HttpServletResponse response) {

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            //创建excel文档
            Workbook workbook = ExcelUtils.createWorkBook(excelDatas);
            if (null == workbook) {
                return;
            }
            workbook.write(os);
            byte[] content = os.toByteArray();
            InputStream is = new ByteArrayInputStream(content);
            // 设置response参数，可以打开下载页面
            response.reset();
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename="
                    + new String((fileName + ".xls").getBytes(),
                    "iso-8859-1"));
            ServletOutputStream out = response.getOutputStream();
            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(out);
            byte[] buff = new byte[2048];
            int bytesRead;
            // Simple read/write loop.
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (final IOException e) {
            log.error("excel导出失败", e);
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    log.error("关闭bis异常", e);
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    log.error("关闭bos异常", e);
                }
            }
        }
    }

    private static Object getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        } else {
            CellType cellType = cell.getCellTypeEnum();
            if (cellType.equals(CellType.STRING)) {
                return StringUtils.trimToEmpty(cell.getStringCellValue());
            } else if (cellType.equals(CellType.NUMERIC)) {
                boolean b = DateUtil.isCellDateFormatted(cell);
                if (b) {
                    return DateUtils.dateToStr(cell.getDateCellValue(),"yyyy-MM-dd HH:mm:ss");
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
//        List<Map<String, Object>> rightList = new ArrayList<>();
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
//        List<ErrorCellBean> errorCellBeanList = filter(map, set);
        List<Map<String, Object>> list = importExcel(inputStream, paramBeanList, set);
        System.out.println(set.size());
        System.out.println(list.size());
    }
}
