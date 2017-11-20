package com.yoogurt.taxi.user.controller.web;

import com.yoogurt.taxi.common.constant.CacheKey;
import com.yoogurt.taxi.common.controller.BaseController;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.helper.RedisHelper;
import com.yoogurt.taxi.common.helper.excel.CellPropertyBean;
import com.yoogurt.taxi.common.helper.excel.ErrorCellBean;
import com.yoogurt.taxi.common.helper.excel.ExcelParamBean;
import com.yoogurt.taxi.common.helper.excel.ExcelUtils;
import com.yoogurt.taxi.common.utils.BeanUtilsExtends;
import com.yoogurt.taxi.common.utils.Encipher;
import com.yoogurt.taxi.common.utils.RandomUtils;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.*;
import com.yoogurt.taxi.dal.bo.SmsPayload;
import com.yoogurt.taxi.dal.condition.user.AuthorityWLCondition;
import com.yoogurt.taxi.dal.condition.user.DriverWLCondition;
import com.yoogurt.taxi.dal.condition.user.UserWLCondition;
import com.yoogurt.taxi.dal.enums.*;
import com.yoogurt.taxi.dal.model.user.GroupAuthorityLModel;
import com.yoogurt.taxi.dal.model.user.RoleWLModel;
import com.yoogurt.taxi.user.form.*;
import com.yoogurt.taxi.user.mq.SmsSender;
import com.yoogurt.taxi.user.service.*;
import com.yoogurt.taxi.user.service.rest.RestAuthService;
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
 * 后台用户controller
 */
@RestController
@RequestMapping("/web/user")
public class UserWebController extends BaseController {

    @Autowired
    private UserService userService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private DriverService driverService;
    @Autowired
    private CarService carService;
    @Autowired
    private RoleAuthorityService roleAuthorityService;
    @Autowired
    private AuthorityInfoService authorityInfoService;
    @Autowired
    private RoleInfoService roleInfoService;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private RestAuthService restAuthService;
    @Autowired
    private SmsSender smsSender;

    @RequestMapping("/tt")
    public String tt() {
        return "tt";
    }

    /**
     * 司机导入
     *
     * @param file excel源文件
     * @return ResponseObj
     * @throws IOException            文件读写异常
     * @throws InvalidFormatException 类型转换异常
     */
    @RequestMapping(value = "/import/agentDrivers", method = RequestMethod.POST, produces = {"application/json;UTF-8"})
    public ResponseObj importAgentUserFromExcel(MultipartFile file) throws IOException, InvalidFormatException {
        List<ExcelParamBean> paramBeanList = new ArrayList<>();
        ExcelParamBean bean1 = new ExcelParamBean(0, "name", "^[\\u4e00-\\u9fa5\\s]{2,8}$", "姓名长度在2-8个中文字符", Boolean.FALSE, null);
        ExcelParamBean bean2 = new ExcelParamBean(1, "idCard", "^(\\d{18}$|^\\d{17}(\\d|X|x))$", "身份证格式有误", Boolean.TRUE, "表格存在身份证号重复");
        ExcelParamBean bean3 = new ExcelParamBean(2, "phoneNumber", "^(\\+\\d+)?1[34578]\\d{9}$", "手机号格式有误", Boolean.TRUE, "表格存在手机号重复");
        ExcelParamBean bean4 = new ExcelParamBean(3, "drivingLicense", "^\\S{1,}$", "不能为空", Boolean.FALSE, null);
        ExcelParamBean bean5 = new ExcelParamBean(4, "serviceNumber", "^\\S{1,}$", "不能为空", Boolean.FALSE, null);
        paramBeanList.add(bean1);
        paramBeanList.add(bean2);
        paramBeanList.add(bean3);
        paramBeanList.add(bean4);
        paramBeanList.add(bean5);
        Map<ExcelParamBean, List<CellPropertyBean>> map = ExcelUtils.importExcel(file.getInputStream(), paramBeanList);
        Set<Integer> skipSet = new HashSet<>();//忽略跳过行数
        List<ErrorCellBean> errorCellBeanList = ExcelUtils.filter(map, skipSet);//过滤表格中的内容

        if (CollectionUtils.isNotEmpty(errorCellBeanList)) {
            StringBuilder sb = new StringBuilder();
            errorCellBeanList.forEach(e -> sb.append(e.getErrorMessage()).append(":").append(e.getCellValue()).append(";坐标：").append(RandomUtils.letters[e.getColIndex() + 35]).append(e.getRowIndex()).append("  <br/>"));
            return ResponseObj.fail(StatusCode.BIZ_FAILED, sb.toString());
        }

        List<Map<String, Object>> rightList = ExcelUtils.importExcel(file.getInputStream(), paramBeanList, skipSet);

        List<ErrorCellBean> errorCellBeanList1 = userService.importAgentDriversFromExcel(rightList);
        if (CollectionUtils.isNotEmpty(errorCellBeanList1)) {
            StringBuilder sb = new StringBuilder();
            errorCellBeanList1.forEach(e -> sb.append(e.getErrorMessage()).append(":").append(e.getCellValue()).append(";坐标：").append(RandomUtils.letters[e.getColIndex() + 35]).append(e.getRowIndex()).append("  <br/>"));
            return ResponseObj.fail(StatusCode.BIZ_FAILED, sb.toString());
        }
        return ResponseObj.success();
    }

    /**
     * 车主导入
     *
     * @param file 源文件
     * @return ResponseObj
     * @throws IOException            文件读写异常
     * @throws InvalidFormatException 类型转换异常
     */
    @RequestMapping(value = "/import/officeDrivers", method = RequestMethod.POST, produces = {"application/json;UTF-8"})
    public ResponseObj importOfficeUsersFromExcel(MultipartFile file) throws IOException, InvalidFormatException {
        List<ExcelParamBean> paramBeanList = new ArrayList<>();
        ExcelParamBean bean0 = new ExcelParamBean(0, "name", "^[\\u4e00-\\u9fa5\\s]{2,8}$", "姓名长度在2-8中文字符", Boolean.FALSE, null);
        ExcelParamBean bean1 = new ExcelParamBean(1, "idCard", "^(\\d{18}$|^\\d{17}(\\d|X|x))$", "身份证格式有误", Boolean.TRUE, "表格存在身份证号重复");
        ExcelParamBean bean2 = new ExcelParamBean(2, "phoneNumber", "^(\\+\\d+)?1[34578]\\d{9}$", "手机号格式有误", Boolean.TRUE, "表格存在手机号重复");
        ExcelParamBean bean3 = new ExcelParamBean(3, "drivingLicense", "^\\S{1,}$", "不能为空", Boolean.FALSE, null);
        ExcelParamBean bean4 = new ExcelParamBean(4, "serviceNumber", "^\\S{1,20}$", "不能为空，且最大长度为20位", Boolean.FALSE, null);
        ExcelParamBean bean5 = new ExcelParamBean(5, "plateNumber", "^\\S{1,8}$", "不能为空，且最大长度为8位", Boolean.FALSE, null);
        ExcelParamBean bean6 = new ExcelParamBean(6, "vehicleType", "^\\S{1,20}$", "不能为空，且最大长度为8位", Boolean.FALSE, null);
        ExcelParamBean bean7 = new ExcelParamBean(7, "vehicleRegisterTime", "", null, Boolean.FALSE, null);
        ExcelParamBean bean8 = new ExcelParamBean(8, "company", "^\\S{1,20}$", "不能为空，且最大长度为8位", Boolean.FALSE, null);
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
            errorCellBeanList.forEach(e -> sb.append(e.getErrorMessage()).append(":").append(e.getCellValue()).append(";坐标：").append(RandomUtils.letters[e.getColIndex() + 35]).append(e.getRowIndex()).append("  <br/>"));
            return ResponseObj.fail(StatusCode.BIZ_FAILED, sb.toString());
        }

        List<Map<String, Object>> rightList = ExcelUtils.importExcel(file.getInputStream(), paramBeanList, skipSet);

        List<ErrorCellBean> errorCellBeanList1 = userService.importOfficeDriversFromExcel(rightList);
        if (CollectionUtils.isNotEmpty(errorCellBeanList1)) {
            StringBuilder sb = new StringBuilder();
            errorCellBeanList1.forEach(e -> sb.append(e.getErrorMessage()).append(":").append(e.getCellValue()).append(";坐标：").append(RandomUtils.letters[e.getColIndex() + 35]).append(e.getRowIndex()).append("  <br/>"));
            return ResponseObj.fail(StatusCode.BIZ_FAILED, sb.toString());
        }
        return ResponseObj.success();
    }

    /**
     * web端登录
     *
     * @param loginForm 表单
     * @return ResponseObj
     */
    @RequestMapping(value = "/i/login", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    public ResponseObj login(@RequestBody LoginForm loginForm) {
        if (StringUtils.isBlank(loginForm.getUsername())) {
            return ResponseObj.fail(StatusCode.PARAM_BLANK, "登录账号不能为空");
        }
        if (StringUtils.isBlank(loginForm.getPassword())) {
            return ResponseObj.fail(StatusCode.PARAM_BLANK, "密码不能为空");
        }
        UserType userType;
        if (loginForm.getUsername().equals("admin")) {
            userType = UserType.USER_WEB;
        } else {
            userType = UserType.SUPER_ADMIN;
        }
        if (!userType.isWebUser()) {
            return ResponseObj.fail(StatusCode.NO_AUTHORITY);
        }
        return loginService.login(loginForm.getUsername(), loginForm.getPassword(), userType);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.PUT, produces = {"application/json;charset=utf-8"})
    public ResponseObj logout() {
        String userId = super.getUserId();
        redisHelper.deleteMap(CacheKey.SHIRO_AUTHORITY_MAP, userId);
        redisHelper.del(CacheKey.SESSION_USER_KEY + userId);
        return ResponseObj.success();
    }

    /**
     * 获取司机分页列表
     *
     * @param driverWLCondition
     * @return
     */
    @RequestMapping(value = "/driver/list", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj getDriverList(@Valid DriverWLCondition driverWLCondition, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, result.getAllErrors().get(0).getDefaultMessage());
        }
        return driverService.getDriverWebList(driverWLCondition);
    }

    /**
     * 获取司机详情
     *
     * @param driverId
     * @return
     */
    @RequestMapping(value = "/driver/detail/driverId/{driverId}", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj getOfficeDriverDetail(@PathVariable(name = "driverId") String driverId) {
        Map<String, Object> map = new HashMap<>();
        DriverInfo driverInfo = driverService.getDriverInfo(driverId);
        List<CarInfo> carInfoList = carService.getCarByDriverId(driverId);
        if (driverInfo != null) {
            map.put("driverInfo", driverInfo);
        }
        UserInfo userInfo = userService.getUserByUserId(driverInfo.getUserId());
        if (userInfo != null) {
            map.put("userInfo", userInfo);
        }
        if (CollectionUtils.isNotEmpty(carInfoList)) {
            map.put("carInfo", carInfoList.get(0));
        }
        return ResponseObj.success(map);
    }

    /**
     * 重置密码
     *
     * @param form
     * @return
     */
    @RequestMapping(value = "/loginPassword", method = RequestMethod.PATCH, produces = {"application/json;charset=utf-8"})
    public ResponseObj resetPassword(@RequestBody UserForm form) {
        String newPassword = RandomUtils.getRandNum(6);
        String userId = form.getUserId();
        UserInfo userInfo = userService.getUserByUserId(userId);
        if (userInfo == null) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "用户不存在");
        }
        userService.resetLoginPwd(userId, DigestUtils.md5Hex(newPassword));
        SmsPayload payload = new SmsPayload();
        if (!UserType.getEnumsByCode(userInfo.getType()).isAppUser()) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "只有app用户方可操作");
        }
        if (userInfo.getType().equals(UserType.USER_APP_AGENT.getCode())) {
            payload.setType(SmsTemplateType.AGENT_RESET_PWD);
        }
        if (userInfo.getType().equals(UserType.USER_APP_OFFICE.getCode())) {
            payload.setType(SmsTemplateType.OFFICE_RESET_PWD);
        }
        payload.setParam(newPassword);
        List<String> phoneNumbers = new ArrayList<>();
        phoneNumbers.add(super.getUserName());
        payload.setPhoneNumbers(phoneNumbers);
        smsSender.send(payload);
        return ResponseObj.success();
    }

    /**
     * 修改登录密码
     *
     * @param form
     * @return
     */
    @RequestMapping(value = "/modifyLoginPwd", method = RequestMethod.PATCH, produces = {"application/json;charset=utf-8"})
    public ResponseObj modifyLoginPwd(@RequestBody UserPatchForm form) {
        if (StringUtils.isBlank(form.getPassword())) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, "旧密码不能为空");
        }
        if (StringUtils.isBlank(form.getNewPassword())) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, "新密码不能为空");
        }
        String userId = super.getUserId();
        UserInfo userInfo = userService.getUserByUserId(userId);
        if (!Encipher.matches(form.getUserId(), userInfo.getLoginPassword())) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "旧密码错误");
        }
        userInfo.setLoginPassword(Encipher.encrypt(form.getNewPassword()));
        return userService.modifyUser(userInfo);
    }

    /**
     * 获取用户列表
     *
     * @param condition
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj getUserWebList(UserWLCondition condition) {
        return ResponseObj.success(userService.getUserWebList(condition));
    }

    @RequestMapping(value = "/modifyOfficeDriver", method = RequestMethod.PATCH, produces = {"application/json;charset=utf-8"})
    public ResponseObj modifyOfficeDriver(@RequestBody @Valid OfficeDriverForm form, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, result.getAllErrors().get(0).getDefaultMessage());
        }
        if (CarEnergyType.getEnumsByCode(form.getEnergyType()) == null) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, "请选择正确的能源类型");
        }
        if (UserGender.getEnumsByCode(form.getGender()) == null) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, "请选择正确的性别");
        }
        UserInfo userInfo = userService.getUserByUserId(form.getUserId());
        if (userInfo == null) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "身份信息不存在");
        }
        DriverInfo driverInfo = driverService.getDriverInfo(form.getDriverId());
        if (driverInfo == null) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "司机信息不存在");
        }
        CarInfo carInfo = carService.getCarInfo(form.getCarId());
        if (carInfo == null) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "车辆信息不存在");
        }
        BeanUtilsExtends.copyProperties(userInfo, form);
        BeanUtilsExtends.copyProperties(driverInfo, form);
        driverInfo.setMobile(form.getUsername());
        BeanUtilsExtends.copyProperties(carInfo, form);
        driverInfo.setIsAuthentication(Boolean.TRUE);
        carInfo.setIsAuthentication(Boolean.TRUE);
        return userService.saveUnitInfo(userInfo, driverInfo, carInfo);
    }

    @RequestMapping(value = "/modifyAgentDriver", method = RequestMethod.PATCH, produces = {"application/json;charset=utf-8"})
    public ResponseObj modifyAgentDriver(@RequestBody @Valid AgentDriverForm form, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, result.getAllErrors().get(0).getDefaultMessage());
        }
        if (UserGender.getEnumsByCode(form.getGender()) == null) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, "请选择正确的性别");
        }
        UserInfo userInfo = userService.getUserByUserId(form.getUserId());
        if (userInfo == null) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "身份信息不存在");
        }
        DriverInfo driverInfo = driverService.getDriverInfo(form.getDriverId());
        if (driverInfo == null) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "司机信息不存在");
        }
        BeanUtilsExtends.copyProperties(userInfo, form);
        BeanUtilsExtends.copyProperties(driverInfo, form);
        driverInfo.setMobile(form.getUsername());
        driverInfo.setIsAuthentication(Boolean.TRUE);
        return userService.saveUnitInfo(userInfo, driverInfo, null);
    }

    /**
     * 功能：冻结
     *
     * @param form
     * @return
     */
    @RequestMapping(value = "/frozen", method = RequestMethod.PATCH, produces = {"application/json;charset=utf-8"})
    public ResponseObj frozenUser(@RequestBody UserPatchForm form) {
        if (form.getUserId() == null) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, "请传入用户id");
        }
        UserInfo userInfo = userService.getUserByUserId(form.getUserId());
        if (userInfo == null) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED);
        }
        if (!userInfo.getStatus().equals(UserStatus.AUTHENTICATED.getCode())) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "用户只有在已认证时才可以冻结");
        }
        userInfo.setStatus(UserStatus.FROZEN.getCode());
        userService.modifyUser(userInfo);
        return ResponseObj.success();
    }

    /**
     * 功能：解冻
     *
     * @param form
     * @return
     */
    @RequestMapping(value = "/unfreeze", method = RequestMethod.PATCH, produces = {"application/json;charset=utf-8"})
    public ResponseObj unfreeze(@RequestBody UserPatchForm form) {
        if (form.getUserId() == null) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, "请传入用户id");
        }
        UserInfo userInfo = userService.getUserByUserId(form.getUserId());
        if (userInfo == null) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED);
        }
        if (!userInfo.getStatus().equals(UserStatus.FROZEN.getCode())) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "用户只有在被冻结时才可以解冻");
        }
        userInfo.setStatus(UserStatus.AUTHENTICATED.getCode());
        userService.modifyUser(userInfo);
        return ResponseObj.success();
    }

    /**
     * 功能：认证
     *
     * @param form
     * @return
     */
    @RequestMapping(value = "/authenticate", method = RequestMethod.PATCH, produces = {"application/json;charset=utf-8"})
    public ResponseObj authenticate(@RequestBody UserPatchForm form) {
        if (form.getUserId() == null) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, "请传入用户id");
        }
        UserInfo userInfo = userService.getUserByUserId(form.getUserId());
        if (userInfo == null) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED);
        }
        DriverInfo driverInfo = driverService.getDriverByUserId(form.getUserId());
        if (!driverInfo.getIsAuthentication()) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "用户资料尚未填写完毕");
        }
        if (!userInfo.getStatus().equals(UserStatus.UN_AUTHENTICATE.getCode())) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "用户只有在未认证才可以认证");
        }
        userInfo.setStatus(UserStatus.AUTHENTICATED.getCode());
        userService.modifyUser(userInfo);
        return ResponseObj.success();
    }

    /**
     * 删除后台用户
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/userId/{userId}", method = RequestMethod.DELETE, produces = {"application/json;charset=utf-8"})
    public ResponseObj removeUser(@PathVariable(name = "userId") String userId) {
        return userService.removeUser(userId);
    }

    /**
     * 新增/编辑后台用户
     *
     * @param form
     * @param result
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    public ResponseObj saveUser(@RequestBody @Valid UserForm form, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, result.getAllErrors().get(0).getDefaultMessage());
        }
        if (userService.getUserByUsernameAndType(form.getUsername(), UserType.USER_WEB.getCode()) != null) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, "账号已存在");
        }
        return userService.saveUser(form);
    }

    /**
     * 获取角色列表
     *
     * @return
     */
    @RequestMapping(value = "/role/list", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj getRoleList() {
        List<RoleWLModel> roleWebList = roleInfoService.getRoleWebList();
        return ResponseObj.success(roleWebList);
    }

    /**
     * 对角色进行授权
     *
     * @param form
     * @param result
     * @return
     */
    @RequestMapping(value = "/grantAuthority", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    public ResponseObj grantAuthority(@RequestBody @Valid RoleAuthorityForm form, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, result.getAllErrors().get(0).getDefaultMessage());
        }
        return roleAuthorityService.saveRoleAuthorityInfo(form.getRoleId(), form.getAuthorityList());
    }

    /**
     * 新增/编辑角色
     *
     * @param form
     * @param result
     * @return
     */
    @RequestMapping(value = "/role", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    public ResponseObj newRole(@RequestBody @Valid RoleForm form, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.PARAM_BLANK, result.getAllErrors().get(0).getDefaultMessage());
        }
        RoleInfo roleInfo = new RoleInfo();
        BeanUtilsExtends.copyProperties(roleInfo, form);
        return roleInfoService.saveRoleInfo(roleInfo);
    }

    /**
     * 删除角色
     *
     * @param roleId
     * @return
     */
    @RequestMapping(value = "/role/roleId/{roleId}", method = RequestMethod.DELETE, produces = {"application/json;charset=utf-8"})
    public ResponseObj removeRole(@PathVariable(name = "roleId") Long roleId) {
        return roleInfoService.removeRole(roleId);
    }

    /**
     * 权限列表
     *
     * @param condition
     * @return
     */
    @RequestMapping(value = "/authority/list", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj getAuthorityList(AuthorityWLCondition condition) {
        return ResponseObj.success(authorityInfoService.getAuthorityWebList(condition));
    }

    /**
     * 获取权限详情
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/authority/id/{id}", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj getAuthorityDetail(@PathVariable(name = "id") Long id) {
        return ResponseObj.success(authorityInfoService.getAuthorityById(id));
    }

    /**
     * 删除权限接口
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/authority/id/{id}", method = RequestMethod.DELETE, produces = {"application/json;charset=utf-8"})
    public ResponseObj removeAuthority(@PathVariable Long id) {
        return authorityInfoService.removeAuthorityById(id);
    }

    /**
     * 新增权限接口
     *
     * @param form
     * @param result
     * @return
     */
    @RequestMapping(value = "/authority", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    public ResponseObj saveAuthority(@RequestBody @Valid AuthorityForm form, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, result.getAllErrors().get(0).getDefaultMessage());
        }
        AuthorityInfo authorityInfo = new AuthorityInfo();
        BeanUtilsExtends.copyProperties(authorityInfo, form);
        return authorityInfoService.saveAuthorityInfo(authorityInfo);
    }

    /**
     * 分组权限接口
     *
     * @return
     */
    @RequestMapping(value = "/authority/group", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj getAuthorityListGroup() {
        List<GroupAuthorityLModel> allAuthorities = authorityInfoService.getAllAuthorities();
        return ResponseObj.success(allAuthorities);
    }

    @RequestMapping(value = "/authority/list/roleId/{roleId}", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj getAuthorityListByRoleId(@PathVariable(name = "roleId") Long roleId) {
        List<GroupAuthorityLModel> authorityInfoList = roleAuthorityService.getAuthorityListByRoleId(roleId);
        return ResponseObj.success(authorityInfoList);
    }

}
