package com.yoogurt.taxi.order.service.rest.hystrix;

import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.vo.RestResult;
import com.yoogurt.taxi.dal.vo.PaymentVo;
import com.yoogurt.taxi.order.service.rest.RestFinanceService;
import com.yoogurt.taxi.pay.doc.Payment;
import org.springframework.stereotype.Service;

@Service
public class RestFinanceServiceImpl implements RestFinanceService {

    @Override
    public RestResult<Payment> updatePayment(PaymentVo paymentVo) {
        return RestResult.fail(StatusCode.BIZ_FAILED, "更新支付对象异常");
    }
}
