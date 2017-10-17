package com.yoogurt.taxi.order.service.impl;

import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.common.vo.RestResult;
import com.yoogurt.taxi.dal.beans.FinanceAccount;
import com.yoogurt.taxi.order.service.OrderBizService;
import com.yoogurt.taxi.order.service.rest.RestAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class AbstractOrderBizService implements OrderBizService {

    @Autowired
    private RestAccountService accountService;

    public ResponseObj isAllowed(Long userId) {

        RestResult<FinanceAccount> accountResult = accountService.getAccountByUserId(userId);
        if (!accountResult.isSuccess()) {
            log.warn("[REST]{}", accountResult.getMessage());
            return ResponseObj.of(accountResult);
        }
        FinanceAccount account = accountResult.getBody();
        if (account.getReceivedDeposit() != null && account.getReceivedDeposit().doubleValue() >= account.getReceivableDeposit().doubleValue()) {
            return ResponseObj.success();
        }
        log.warn("[REST]{}", "押金未充足");
        Map<String, Object> extras = new HashMap<>();
        extras.put("redirect", "charge");
        return ResponseObj.fail(StatusCode.BIZ_FAILED, "您的押金不足，请充值", extras);
    }

}
