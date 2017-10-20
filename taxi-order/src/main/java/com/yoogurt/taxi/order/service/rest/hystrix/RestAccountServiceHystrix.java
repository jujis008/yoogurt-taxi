package com.yoogurt.taxi.order.service.rest.hystrix;

import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.vo.RestResult;
import com.yoogurt.taxi.dal.beans.FinanceAccount;
import com.yoogurt.taxi.dal.vo.ModificationVo;
import com.yoogurt.taxi.order.service.rest.RestAccountService;

public class RestAccountServiceHystrix implements RestAccountService {

    @Override
    public RestResult<FinanceAccount> getAccountByUserId(Long userId) {
        return RestResult.fail(StatusCode.BIZ_FAILED, "获取账户信息异常");
    }

    @Override
    public RestResult updateAccount(ModificationVo vo) {
        return RestResult.fail(StatusCode.BIZ_FAILED, "更新账户信息异常");
    }
}
