package com.yoogurt.taxi.user.controller.web;

import com.yoogurt.taxi.common.helper.excel.CellPropertyBean;
import com.yoogurt.taxi.common.helper.excel.ErrorCellBean;
import com.yoogurt.taxi.common.helper.excel.ExcelParamBean;
import com.yoogurt.taxi.common.helper.excel.ExcelUtils;
import com.yoogurt.taxi.common.constant.CacheKey;
import com.yoogurt.taxi.common.controller.BaseController;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.helper.RedisHelper;
import com.yoogurt.taxi.common.utils.BeanUtilsExtends;
import com.yoogurt.taxi.common.utils.RandomUtils;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.*;
import com.yoogurt.taxi.dal.condition.user.AuthorityWLCondition;
import com.yoogurt.taxi.dal.condition.user.DriverWLCondition;
import com.yoogurt.taxi.dal.condition.user.UserWLCondition;
import com.yoogurt.taxi.dal.enums.UserType;
import com.yoogurt.taxi.dal.model.user.GroupAuthorityLModel;
import com.yoogurt.taxi.dal.model.user.RoleWLModel;
import com.yoogurt.taxi.user.form.*;
import com.yoogurt.taxi.user.service.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

/**
 * Description:
 * 后台用户管理接口
 * @Author Eric Lau
 * @Date 2017/9/4.
 */
@RestController
@RequestMapping("/web/user")
public class UserWebController extends BaseController{

    @Autowired
    private UserService userService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private DriverService   driverService;
    @Autowired
    private CarService carService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private RoleAuthorityService    roleAuthorityService;
    @Autowired
    private AuthorityInfoService    authorityInfoService;
    @Autowired
    private RoleInfoService roleInfoService;
    @Autowired
    private RedisHelper redisHelper;

    @RequestMapping("/tt")
    public String tt() {
        return "tt";
    }

    /**
     * 代理司机导入
     * @param file
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
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

    /**
     * 正式司机导入
     * @param file
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
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

    /**
     *web端登录
     * @param loginForm
     * @return
     */
    @RequestMapping(value = "/i/login",method = RequestMethod.POST,produces = {"application/json;charset=utf-8"})
    public ResponseObj login(@RequestBody LoginForm loginForm) {
        if (StringUtils.isBlank(loginForm.getUsername())) {
            return ResponseObj.fail(StatusCode.PARAM_BLANK,"登录账号不能为空");
        }
        if (StringUtils.isBlank(loginForm.getPassword())) {
            return ResponseObj.fail(StatusCode.PARAM_BLANK,"密码不能为空");
        }
        UserType userType = UserType.getEnumsByCode(getUserType());
        if (!userType.isWebUser()) {
            return ResponseObj.fail(StatusCode.NO_AUTHORITY);
        }
        ResponseObj result = loginService.login(loginForm.getUsername(), loginForm.getPassword(), userType);
        return result;
    }

    /**
     * 获取司机分页列表
     * @param driverWLCondition
     * @return
     */
    @RequestMapping(value = "/driver/list",method = RequestMethod.GET,produces = {"application/json;charset=utf-8"})
    public ResponseObj getDriverList(@Valid DriverWLCondition driverWLCondition,BindingResult result) {
        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID,result.getAllErrors().get(0).getDefaultMessage());
        }
        return driverService.getDriverWebList(driverWLCondition);
    }

    /**
     * 获取司机详情
     * @param driverId
     * @return
     */
    @RequestMapping(value = "/driver/detail/driverId/{driverId}",method = RequestMethod.GET,produces = {"application/json;charset=utf-8"})
    public ResponseObj getOfficeDriverDetail(@PathVariable(name = "driverId") Long driverId) {
        DriverInfo driverInfo = driverService.getDriverInfo(driverId);
        List<CarInfo> carInfoList = carService.getCarByDriverId(driverId);
        UserInfo userInfo = userService.getUserByUserId(driverInfo.getUserId());
        Map<String,Object> map = new HashMap<>();
        if (userInfo != null) {
            map.put("userInfo",userInfo);
        }
        if (driverInfo != null) {
            map.put("driverInfo",driverInfo);
        }
        if (CollectionUtils.isNotEmpty(carInfoList)) {
            map.put("carInfo",carInfoList.get(0));
        }
        return ResponseObj.success(map);
    }

    /**
     * 重置密码
     * @param userId
     * @return
     */
    @RequestMapping(value = "/loginPassword",method = RequestMethod.PATCH,produces = {"application/json;charset=utf-8"})
    public ResponseObj resetPassword(@RequestBody Long userId) {
        String newPassword = RandomUtils.getRandNum(6);
        UserInfo userInfo = userService.getUserByUserId(userId);
        userService.resetLoginPwd(userId, DigestUtils.md5Hex(newPassword));
        redisHelper.set(CacheKey.VERIFY_CODE_KEY+userInfo.getUsername(), newPassword,5*60);
        return ResponseObj.success();
    }

    /**
     * 获取用户列表
     * @param condition
     * @return
     */
    @RequestMapping(value = "/list",method = RequestMethod.GET,produces = {"application/json;charset=utf-8"})
    public ResponseObj getUserWebList(UserWLCondition condition) {
        return ResponseObj.success(userService.getUserWebList(condition));
    }

    /**
     * 删除后台用户
     * @param userId
     * @return
     */
    @RequestMapping(value = "/userId/{userId}",method = RequestMethod.DELETE,produces = {"application/json;charset=utf-8"})
    public ResponseObj removeUser(@PathVariable(name = "userId") Long userId) {
        return userService.removeUser(userId);
    }

    /**
     * 新增/编辑后台用户
     * @param form
     * @param result
     * @return
     */
    @RequestMapping(value = "/",method = RequestMethod.POST,produces = {"application/json;charset=utf-8"})
    public ResponseObj saveUser(@RequestBody @Valid UserForm form, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID,result.getAllErrors().get(0).getDefaultMessage());
        }
        if(userService.getUserByUsernameAndType(form.getUsername(),UserType.USER_WEB.getCode())!=null) {
            return ResponseObj.fail(StatusCode.FORM_INVALID,"账号已存在");
        }
        return userService.saveUser(form);
    }

    /**
     * 获取角色列表
     * @return
     */
    @RequestMapping(value = "/role/list",method = RequestMethod.GET,produces = {"application/json;charset=utf-8"})
    public ResponseObj getRoleList() {
        List<RoleWLModel> roleWebList = roleInfoService.getRoleWebList();
        return ResponseObj.success(roleWebList);
    }

    /**
     * 对角色进行授权
     * @param form
     * @param result
     * @return
     */
    @RequestMapping(value = "/grantAuthority",method = RequestMethod.POST,produces = {"application/json;charset=utf-8"})
    public ResponseObj grantAuthority(@RequestBody @Valid RoleAuthorityForm form, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID,result.getAllErrors().get(0).getDefaultMessage());
        }
        return roleAuthorityService.saveRoleAuthorityInfo(form.getRoleId(),form.getAuthorityList());
    }

    /**
     * 新增/编辑角色
     * @param form
     * @param result
     * @return
     */
    @RequestMapping(value = "/role",method = RequestMethod.POST,produces = {"application/json;charset=utf-8"})
    public ResponseObj newRole(@RequestBody @Valid RoleForm form, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.PARAM_BLANK,result.getAllErrors().get(0).getDefaultMessage());
        }
        RoleInfo roleInfo = new RoleInfo();
        BeanUtilsExtends.copyProperties(roleInfo,form);
        return roleInfoService.saveRoleInfo(roleInfo);
    }

    /**
     * 删除角色
     * @param roleId
     * @return
     */
    @RequestMapping(value = "/role/roleId/{roleId}",method = RequestMethod.DELETE,produces = {"application/json;charset=utf-8"})
    public ResponseObj removeRole(@PathVariable(name = "roleId") Long roleId) {
        return roleInfoService.removeRole(roleId);
    }

    /**
     * 权限列表
     * @param condition
     * @return
     */
    @RequestMapping(value = "/authority/list",method = RequestMethod.GET,produces = {"application/json;charset=utf-8"})
    public ResponseObj getAuthorityList(AuthorityWLCondition condition) {
        return ResponseObj.success(authorityInfoService.getAuthorityWebList(condition));
    }

    /**
     * 获取权限详情
     * @param id
     * @return
     */
    @RequestMapping(value = "/authority/id/{id}",method = RequestMethod.GET,produces = {"application/json;charset=utf-8"})
    public ResponseObj getAuthorityDetail(@PathVariable(name = "id") Long id) {
        return ResponseObj.success(authorityInfoService.getAuthorityById(id));
    }

    /**
     * 删除权限接口
     * @param id
     * @return
     */
    @RequestMapping(value = "/authority/id/{id}",method = RequestMethod.DELETE,produces = {"application/json;charset=utf-8"})
    public ResponseObj removeAuthority(@PathVariable Long id) {
        return authorityInfoService.removeAuthorityById(id);
    }

    /**
     * 新增权限接口
     * @param form
     * @param result
     * @return
     */
    @RequestMapping(value = "/authority",method = RequestMethod.POST,produces = {"application/json;charset=utf-8"})
    public ResponseObj saveAuthority(@RequestBody @Valid AuthorityForm form,BindingResult result) {
        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID,result.getAllErrors().get(0).getDefaultMessage());
        }
        AuthorityInfo authorityInfo = new AuthorityInfo();
        BeanUtilsExtends.copyProperties(authorityInfo,form);
        return authorityInfoService.saveAuthorityInfo(authorityInfo);
    }

    /**
     * 分组权限接口
     * @return
     */
    @RequestMapping(value = "/authority/group",method = RequestMethod.GET,produces = {"application/json;charset=utf-8"})
    public ResponseObj getAuthorityListGroup() {
        List<GroupAuthorityLModel> allAuthorities = authorityInfoService.getAllAuthorities();
        return ResponseObj.success(allAuthorities);
    }

}
