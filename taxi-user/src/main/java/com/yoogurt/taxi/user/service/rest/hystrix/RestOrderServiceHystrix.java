package com.yoogurt.taxi.user.service.rest.hystrix;

import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.vo.RestResult;
import com.yoogurt.taxi.user.service.rest.RestOrderService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RestOrderServiceHystrix implements RestOrderService {
    @Override
    public RestResult<Map<String, Object>> statistics(String userId) {
        return RestResult.fail(StatusCode.REST_FAIL, "获取订单统计信息失败");
    }
}
