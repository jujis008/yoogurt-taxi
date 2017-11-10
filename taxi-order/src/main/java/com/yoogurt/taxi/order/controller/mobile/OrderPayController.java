package com.yoogurt.taxi.order.controller.mobile;

import com.yoogurt.taxi.common.controller.BaseController;
import com.yoogurt.taxi.common.enums.MessageQueue;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.utils.CommonUtils;
import com.yoogurt.taxi.common.utils.RandomUtils;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.OrderInfo;
import com.yoogurt.taxi.dal.beans.OrderPayment;
import com.yoogurt.taxi.dal.enums.PayChannel;
import com.yoogurt.taxi.order.form.PayForm;
import com.yoogurt.taxi.order.service.OrderInfoService;
import com.yoogurt.taxi.order.service.OrderPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;

@RestController
@RequestMapping("/mobile/order")
public class OrderPayController extends BaseController {

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private OrderPaymentService paymentService;

    @RequestMapping(value = "/payment", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    public ResponseObj doPay(@Valid @RequestBody PayForm form, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, result.getAllErrors().get(0).getDefaultMessage());
        }
        Long orderId = form.getOrderId();
        OrderInfo orderInfo = orderInfoService.getOrderInfo(form.getOrderId(), super.getUserId());
        if (orderInfo == null) return ResponseObj.fail(StatusCode.BIZ_FAILED, "订单记录不存在");
        if (orderInfo.getIsPaid()) return ResponseObj.fail(StatusCode.BIZ_FAILED, "订单已支付");
        PayChannel channel = PayChannel.getChannelByName(form.getPayChannel());
        if (channel == null || !channel.isThirdParty()) return ResponseObj.fail(StatusCode.BIZ_FAILED, "支付渠道不支持");
        String payId = "pay_" + RandomUtils.getPrimaryKey();
        OrderPayment payment = new OrderPayment(payId, orderId);
        payment.setSubject("替你开-" + CommonUtils.convertName(orderInfo.getOfficialDriverName(), "师傅"));
        payment.setBody("替你开-订单编号" + form.getOrderId());
        payment.setPayChannel(form.getPayChannel());
        payment.setStatus(10); //支付未完成
        payment.setAmount(orderInfo.getAmount());
        if (paymentService.addPayment(payment) != null) {

            return ResponseObj.success(payment, new HashMap<String, Object>() {{
                put("biz", MessageQueue.ORDER_NOTIFY_QUEUE.getBiz());
            }});
        }
        return ResponseObj.fail(StatusCode.SYS_ERROR, StatusCode.SYS_ERROR.getDetail());
    }

    @RequestMapping(value = "/payment/{payId}", method = RequestMethod.DELETE, produces = {"application/json;charset=utf-8"})
    public ResponseObj removePayment(@PathVariable(name = "payId") String payId) {
        OrderPayment payment = paymentService.getPayment(payId);
        //只有未完成的支付记录才能被删除
        if (payment != null && payment.getStatus() == 10 && paymentService.deletePayment(payId)) {
            return ResponseObj.success();
        }
        return ResponseObj.fail();
    }
}
