package com.yoogurt.taxi.user.controller.web;

import com.yoogurt.taxi.common.ExcelHelper.CellPropertyBean;
import com.yoogurt.taxi.common.ExcelHelper.ErrorCellBean;
import com.yoogurt.taxi.common.ExcelHelper.ExcelParamBean;
import com.yoogurt.taxi.common.ExcelHelper.ExcelUtils;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.utils.RandomUtils;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.DriverInfo;
import com.yoogurt.taxi.dal.beans.UserInfo;
import com.yoogurt.taxi.dal.enums.UserFrom;
import com.yoogurt.taxi.dal.enums.UserGender;
import com.yoogurt.taxi.dal.enums.UserStatus;
import com.yoogurt.taxi.dal.enums.UserType;
import com.yoogurt.taxi.user.Form.LoginForm;
import com.yoogurt.taxi.user.service.LoginService;
import com.yoogurt.taxi.user.service.UserService;
import jdk.internal.util.xml.impl.Input;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Description:
 * 后台用户管理接口
 * @Author Eric Lau
 * @Date 2017/9/4.
 */
@RestController
@RequestMapping("/web/user")
public class UserWebController {

    @Autowired
    private UserService userService;

    @RequestMapping("/tt")
    public String tt() {
        return "tt";
    }

    @RequestMapping(value = "/import/agentDrivers",method = RequestMethod.POST,produces = {"application/json;UTF-8"})
    public ResponseObj importAgentUserFromExcel(MultipartFile file) throws IOException, InvalidFormatException {
        List<ExcelParamBean> paramBeanList = new ArrayList<>();
        ExcelParamBean bean1 = new ExcelParamBean(0, "name", "^[\\u4e00-\\u9fa5\\s]{2,8}$", "姓名长度在2-8个字符", Boolean.FALSE, null);
        ExcelParamBean bean2 = new ExcelParamBean(1, "idCard", "^(\\d{18}$|^\\d{17}(\\d|X|x))$", "身份证格式有误", Boolean.TRUE, "表格存在身份证号重复");
        ExcelParamBean bean3 = new ExcelParamBean(2, "phoneNumber","^(\\+\\d+)?1[34578]\\d{9}$", "手机号格式有误", Boolean.TRUE, "表格存在手机号重复");
        ExcelParamBean bean4 = new ExcelParamBean(3, "drivingLicense","^\\S{1,}$", "不能为空", Boolean.FALSE, null);
        paramBeanList.add(bean1);
        paramBeanList.add(bean2);
        paramBeanList.add(bean3);
        paramBeanList.add(bean4);
        Map<ExcelParamBean, List<CellPropertyBean>> map = ExcelUtils.importExcel(file.getInputStream(), paramBeanList);
        Set<Integer> skipSet = new HashSet<>();//忽略跳过行数
        List<ErrorCellBean> errorCellBeanList = ExcelUtils.filter(map, skipSet);//过滤表格中的内容

        if (CollectionUtils.isNotEmpty(errorCellBeanList)) {
            StringBuilder sb = new StringBuilder();
            errorCellBeanList.forEach(e->sb.append(e.getErrorMessage()).append(":").append(e.getCellValue()).append(";坐标：").append(RandomUtils.letters[e.getColIndex()+35]).append(e.getRowIndex()).append("  <br/>"));
            return ResponseObj.fail(StatusCode.BIZ_FAILED,sb.toString());
        }

        List<Map<String, Object>> rightList = ExcelUtils.importExcel(file.getInputStream(), paramBeanList, skipSet);

        List<ErrorCellBean> errorCellBeanList1 = userService.importAgentDriversFromExcel(rightList);
        if (CollectionUtils.isNotEmpty(errorCellBeanList1)) {
            StringBuilder sb = new StringBuilder();
            errorCellBeanList1.forEach(e->sb.append(e.getErrorMessage()).append(":").append(e.getCellValue()).append(";坐标：").append(RandomUtils.letters[e.getColIndex()+35]).append(e.getRowIndex()).append("  <br/>"));
            return ResponseObj.fail(StatusCode.BIZ_FAILED,sb.toString());
        }
        return ResponseObj.success();
    }

    @RequestMapping(value = "/import/officeDrivers",method = RequestMethod.POST,produces = {"application/json;UTF-8"})
    public ResponseObj importOfficeUsersFromExcel(MultipartFile file) throws IOException, InvalidFormatException {
        List<ExcelParamBean> paramBeanList = new ArrayList<>();
        ExcelParamBean bean0 = new ExcelParamBean(0, "name", "^[\\u4e00-\\u9fa5\\s]{2,8}$", "姓名长度在2-8个字符", Boolean.FALSE, null);
        ExcelParamBean bean1 = new ExcelParamBean(1, "idCard", "^(\\d{18}$|^\\d{17}(\\d|X|x))$", "身份证格式有误", Boolean.TRUE, "表格存在身份证号重复");
        ExcelParamBean bean2 = new ExcelParamBean(2, "phoneNumber","^(\\+\\d+)?1[34578]\\d{9}$", "手机号格式有误", Boolean.TRUE, "表格存在手机号重复");
        ExcelParamBean bean3 = new ExcelParamBean(3, "drivingLicense","^\\S{1,}$", "不能为空", Boolean.FALSE, null);
        ExcelParamBean bean4 = new ExcelParamBean(4, "serviceNumber","^\\S{1,20}$", "不能为空，且最大长度为20位", Boolean.FALSE, null);
        ExcelParamBean bean5 = new ExcelParamBean(5, "plateNumber","^\\S{1,8}$", "不能为空，且最大长度为8位", Boolean.FALSE, null);
        ExcelParamBean bean6 = new ExcelParamBean(6, "vehicleType","^\\S{1,20}$", "不能为空，且最大长度为8位", Boolean.FALSE, null);
        ExcelParamBean bean7 = new ExcelParamBean(7, "vehicleRegisterTime","", null, Boolean.FALSE, null);
        ExcelParamBean bean8 = new ExcelParamBean(8, "company","^\\S{1,20}$", "不能为空，且最大长度为8位", Boolean.FALSE, null);
        paramBeanList.add(bean0);
        paramBeanList.add(bean1);
        paramBeanList.add(bean2);
        paramBeanList.add(bean3);
        paramBeanList.add(bean4);
        paramBeanList.add(bean5);
        paramBeanList.add(bean6);
        paramBeanList.add(bean7);
        paramBeanList.add(bean8);
        Map<ExcelParamBean, List<CellPropertyBean>> map = ExcelUtils.importExcel(file.getInputStream(), paramBeanList);

        Set<Integer> skipSet = new HashSet<>();//忽略跳过行数
        List<ErrorCellBean> errorCellBeanList = ExcelUtils.filter(map, skipSet);//过滤表格中的内容

        if (CollectionUtils.isNotEmpty(errorCellBeanList)) {
            StringBuilder sb = new StringBuilder();
            errorCellBeanList.forEach(e->sb.append(e.getErrorMessage()).append(":").append(e.getCellValue()).append(";坐标：").append(RandomUtils.letters[e.getColIndex()+35]).append(e.getRowIndex()).append("  <br/>"));
            return ResponseObj.fail(StatusCode.BIZ_FAILED,sb.toString());
        }

        List<Map<String, Object>> rightList = ExcelUtils.importExcel(file.getInputStream(), paramBeanList, skipSet);

        List<ErrorCellBean> errorCellBeanList1 = userService.importOfficeDriversFromExcel(rightList);
        if (CollectionUtils.isNotEmpty(errorCellBeanList1)) {
            StringBuilder sb = new StringBuilder();
            errorCellBeanList1.forEach(e->sb.append(e.getErrorMessage()).append(":").append(e.getCellValue()).append(";坐标：").append(RandomUtils.letters[e.getColIndex()+35]).append(e.getRowIndex()).append("  <br/>"));
            return ResponseObj.fail(StatusCode.BIZ_FAILED,sb.toString());
        }
        return ResponseObj.success();
    }

}
