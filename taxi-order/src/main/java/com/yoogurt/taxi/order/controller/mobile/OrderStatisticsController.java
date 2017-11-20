package com.yoogurt.taxi.order.controller.mobile;

import com.yoogurt.taxi.common.constant.Constants;
import com.yoogurt.taxi.common.controller.BaseController;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.common.vo.RestResult;
import com.yoogurt.taxi.dal.beans.FinanceAccount;
import com.yoogurt.taxi.dal.beans.RentInfo;
import com.yoogurt.taxi.dal.beans.UserInfo;
import com.yoogurt.taxi.dal.enums.RentStatus;
import com.yoogurt.taxi.dal.enums.UserStatus;
import com.yoogurt.taxi.order.service.RentInfoService;
import com.yoogurt.taxi.order.service.rest.RestAccountService;
import com.yoogurt.taxi.order.service.rest.RestUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/mobile/order")
public class OrderStatisticsController extends BaseController {

    @Autowired
    private RentInfoService rentInfoService;

    @Autowired
    private RestUserService userService;

    @Autowired
    private RestAccountService accountService;

    @RequestMapping(value = "/statistics", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj getStatistics() {

        Map<String, Object> map = new HashMap<>();

        String userId = super.getUserId();
        List<RentInfo> rentList = rentInfoService.getRentList(userId, RentStatus.WAITING.getCode(), RentStatus.RENT.getCode());
        map.put("maxRentCount", Constants.MAX_RENT_COUNT);
        map.put("rentCount", rentList.size());

        Integer userType = 0;

        RestResult<UserInfo> userResult = userService.getUserInfoById(userId);
        if (userResult.isSuccess()) {
            UserInfo userInfo = userResult.getBody();
            userType = userInfo.getType();
            map.put("userStatus", userInfo.getStatus());
        } else {
            map.put("userStatus", UserStatus.AUTHENTICATED.getCode());
        }

        RestResult<FinanceAccount> accountResult = accountService.getAccountByUserId(userId, userType);
        if (accountResult.isSuccess()) {
            FinanceAccount account = accountResult.getBody();
            map.put("receivedDeposit", account.getReceivedDeposit());
            map.put("receivableDeposit", account.getReceivableDeposit());
            map.put("enough", account.getReceivedDeposit() != null && account.getReceivedDeposit().doubleValue() >= account.getReceivableDeposit().doubleValue());
        } else {
            map.put("receivedDeposit", 0.00);
            map.put("receivableDeposit", 0.00);
            map.put("enough", true);
        }

        Map<String, Object> extras = new HashMap<String, Object>() {{
            put("timestamp", System.currentTimeMillis());
        }};
        return ResponseObj.success(map, extras);
    }
}
