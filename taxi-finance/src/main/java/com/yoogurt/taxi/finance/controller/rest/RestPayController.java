package com.yoogurt.taxi.finance.controller.rest;

import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.vo.RestResult;
import com.yoogurt.taxi.dal.doc.finance.Payment;
import com.yoogurt.taxi.dal.vo.PaymentVo;
import com.yoogurt.taxi.finance.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/finance")
public class RestPayController {

    @Autowired
    private PayService payService;

    @RequestMapping(value = "/payment", method = RequestMethod.PUT, produces = {"application/json;charset=utf-8"})
    public RestResult<Payment> updatePayment(@RequestBody PaymentVo paymentVo) {
        Payment payment = payService.getPayment(paymentVo.getPayId());
        if(payment == null) return RestResult.fail(StatusCode.BIZ_FAILED, "找不到支付对象");
        payment.setPaid(true);
        payment.setPaidAmount(paymentVo.getPaidAmount());
        payment.setPaidTime(paymentVo.getPaidTime());
        payment.setTransactionNo(paymentVo.getTransactionNo());
        payment.setStatusCode("SUCCESS");
        payment.setMessage("支付成功");
        if (payService.updatePayment(payment) != null) {
            return RestResult.success(payment);
        }
        return RestResult.fail(StatusCode.BIZ_FAILED, "支付失败");
    }
}
