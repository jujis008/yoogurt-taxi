package com.yoogurt.taxi.order.controller.mobile;

import com.yoogurt.taxi.common.condition.PageableCondition;
import com.yoogurt.taxi.common.controller.BaseController;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.RentInfo;
import com.yoogurt.taxi.dal.condition.order.RentListCondition;
import com.yoogurt.taxi.dal.condition.order.RentPOICondition;
import com.yoogurt.taxi.order.form.RentCancelForm;
import com.yoogurt.taxi.order.form.RentForm;
import com.yoogurt.taxi.order.service.RentInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/mobile/order")
public class RentMobileController extends BaseController {

    @Autowired
    private RentInfoService rentInfoService;

    /**
     * 地图POI检索
     * @param condition 检索条件
     * @return ResponseObj
     */
    @RequestMapping(value = "/i/rents/poi", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj getRentList(RentPOICondition condition) {

        if(!condition.validate()) return ResponseObj.fail(StatusCode.FORM_INVALID, "查询条件有误");
        return ResponseObj.success(rentInfoService.getRentList(condition));
    }

    /**
     * 列表检索
     * @param condition 检索条件
     * @return ResponseObj
     */
    @RequestMapping(value = "/i/rents", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj getRentList(RentListCondition condition) {

        if(!condition.validate()) return ResponseObj.fail(StatusCode.FORM_INVALID, "查询条件有误");
        condition.setFromApp(true);
        return ResponseObj.success(rentInfoService.getRentListByPage(condition));
    }

    /**
     * 我发布的租单列表
     * @param condition 分页条件
     * @return ResponseObj
     */
    @RequestMapping(value = "/rents", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj getRentList(PageableCondition condition) {

        if(!condition.validate()) return ResponseObj.fail(StatusCode.FORM_INVALID, "查询条件有误");
        RentListCondition c = new RentListCondition();
        c.setUserId(super.getUserId());
        c.setFromApp(true);
        c.setPageSize(condition.getPageSize());
        c.setPageNum(condition.getPageNum());
        return ResponseObj.success(rentInfoService.getRentListByPage(c));
    }

    /**
     * 发布租单
     * @param rentForm 发布信息表单
     * @param result 校验结果
     * @return ResponseObj
     */
    @RequestMapping(value = "/rent", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    public ResponseObj publishRentInfo(@Valid @RequestBody RentForm rentForm, BindingResult result) {

        if(result.hasErrors()) return ResponseObj.fail(StatusCode.FORM_INVALID, result.getAllErrors().get(0).getDefaultMessage());
        rentForm.setUserId(getUserId());
        ResponseObj obj = rentInfoService.addRentInfo(rentForm);
        if (obj.getExtras() != null) {
            obj.getExtras().put("timestamp", System.currentTimeMillis());
        }
        return obj;
    }

    /**
     * 获取租单详情
     * @param rentId 租单ID
     * @return ResponseObj
     */
    @RequestMapping(value = "/rent/info/{rentId}", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj getRentInfo(@PathVariable(name = "rentId") Long rentId) {

        RentInfo rentInfo = rentInfoService.getRentInfo(rentId, getUserId());
        if(rentInfo != null) {
            Map<String, Object> extras = new HashMap<>();
            extras.put("timestamp", System.currentTimeMillis());
            return ResponseObj.success(rentInfo, extras);
        }
        return ResponseObj.fail(StatusCode.BIZ_FAILED, "找不到租单信息");
    }

    @RequestMapping(value = "/rent/cancel", method = RequestMethod.PATCH, produces = {"application/json;charset=utf-8"})
    public ResponseObj cancel(@Valid @RequestBody RentCancelForm cancelForm, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, result.getAllErrors().get(0).getDefaultMessage());
        }
        cancelForm.setUserId(super.getUserId());
        RentInfo rentInfo = rentInfoService.cancel(cancelForm);
        if (rentInfo != null) {
            return ResponseObj.success(rentInfo);
        }
        return ResponseObj.fail(StatusCode.BIZ_FAILED, "取消租单出现问题，请稍后再试");
    }
}
