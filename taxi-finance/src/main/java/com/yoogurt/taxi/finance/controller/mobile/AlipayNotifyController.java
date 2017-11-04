package com.yoogurt.taxi.finance.controller.mobile;

import com.yoogurt.taxi.finance.service.AlipayService;
import com.yoogurt.taxi.finance.service.impl.AlipayNotifyServiceImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 程序执行完后必须打印输出“success”（不包含引号）。
 * 如果商户反馈给支付宝的字符不是success这7个字符，支付宝服务器会不断重发通知，直到超过24小时22分钟。
 * 一般情况下，25小时以内完成8次通知（通知的间隔频率一般是：4m,10m,10m,1h,2h,6h,15h）。
 */
@RestController
@RequestMapping("/webhooks/finance")
public class AlipayNotifyController extends NotifyController<AlipayService, AlipayNotifyServiceImpl> {


    /**
     * 支付宝App支付成功的回调接口
     *
     * @param request  回调请求对象
     * @param response 响应对象
     */
    @RequestMapping(value = "/i/alipay", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    public void alipayNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (super.notify(request, response)) {//回调成功
            response.setStatus(HttpServletResponse.SC_OK);
            out.write("success");
            return;
        }
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        out.write("fail");
        out.flush();
    }

}
