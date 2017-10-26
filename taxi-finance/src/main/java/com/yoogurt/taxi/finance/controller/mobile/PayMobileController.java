package com.yoogurt.taxi.finance.controller.mobile;

import com.yoogurt.taxi.common.controller.BaseController;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.utils.CommonUtils;
import com.yoogurt.taxi.common.utils.RegUtil;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.doc.finance.Payment;
import com.yoogurt.taxi.dal.enums.PayChannel;
import com.yoogurt.taxi.finance.form.PayForm;
import com.yoogurt.taxi.finance.service.PayService;
import com.yoogurt.taxi.finance.task.PayTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/mobile/finance")
public class PayMobileController extends BaseController {

    @Autowired
    private PayService payService;

    /**
     * 发起支付的method字段值，固定yoogurt.taxi.finance.pay
     */
    private static final String METHOD = "yoogurt.taxi.finance.pay";

    /**
     * 发起请求的时间与服务器当前时间的最大间隔
     */
    private static final long MAX_REQUEST_INTERVAL = 30 * 60 * 1000; //半小时

    @RequestMapping(value = "/task", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    public ResponseObj submitPayTask(@Valid @RequestBody PayForm payForm, BindingResult result) {

        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, result.getAllErrors().get(0).getDefaultMessage());
        }
        //表单字段进一步验证
        ResponseObj validateResult = validatePayForm(payForm);
        if(!validateResult.isSuccess()) return validateResult;

        PayTask task = payService.submit(payForm);
        if (task != null) {

            return ResponseObj.success(task);
        }
        return ResponseObj.fail(StatusCode.SYS_ERROR, StatusCode.SYS_ERROR.getDetail());
    }

    /**
     * 获取任务信息
     * @param taskId 任务ID
     * @return ResponseObj
     */
    @RequestMapping(value = "/task/{taskId}", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj getTaskInfo(@PathVariable(name = "taskId") String taskId) {
        PayTask task = payService.getTask(taskId);
        if (task != null) {
            return ResponseObj.success(task);
        }
        return ResponseObj.fail(StatusCode.BIZ_FAILED, "未提交该支付任务");
    }

    /**
     * 获取支付对象
     * @param taskId 发起支付任务的ID
     * @return ResponseObj
     */
    @RequestMapping(value = "/payment/{taskId}", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj getPayment(@PathVariable(name = "taskId") String taskId) {
        Payment payment = payService.queryResult(taskId);
        return ResponseObj.success(payment);
    }

    private ResponseObj validatePayForm(PayForm payForm) {
        if (!METHOD.equals(payForm.getMethod())) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, "method error");
        }
        if (!RegUtil.checkIpAddress(payForm.getClientIp())) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, "ip_address error");
        }
        if (System.currentTimeMillis() < payForm.getTimestamp() || System.currentTimeMillis() - payForm.getTimestamp() > MAX_REQUEST_INTERVAL) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, "timestamp error");
        }
        if (CommonUtils.getLength(payForm.getSubject()) > 32) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, "subject字段值过长，请控制在32个Unicode字符以内");
        }
        if (CommonUtils.getLength(payForm.getBody()) > 128) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, "body字段值过长，请控制在128个Unicode字符以内");
        }
        if (CommonUtils.getLength(payForm.getDescription()) > 255) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, "description字段值过长，请控制在128个Unicode字符以内");
        }
        if (payForm.getMetadata() != null && payForm.getMetadata().size() > 10) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, "metadata字段值过长，请控制在10个键值对以内");
        }
        if (PayChannel.getChannelByName(payForm.getChannel()) == null) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, "不支持的支付渠道");
        }
        return ResponseObj.success();
    }
}
