package com.yoogurt.taxi.finance.controller.mobile;

import com.yoogurt.taxi.finance.service.WxPayService;
import com.yoogurt.taxi.finance.service.impl.WxNotifyServiceImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 对后台通知交互时，如果微信收到商户的应答不是成功或超时，微信认为通知失败，
 * 微信会通过一定的策略定期重新发起通知，尽可能提高通知的成功率，但微信不保证通知最终能成功。
 * （通知频率为15/15/30/180/1800/1800/1800/1800/3600，单位：秒）
 * 注意：同样的通知可能会多次发送给商户系统。商户系统必须能够正确处理重复的通知。
 */
@RestController
@RequestMapping("/webhooks/finance")
public class WxNotifyController extends NotifyController<WxPayService, WxNotifyServiceImpl> {

    @RequestMapping(value = "/i/wx", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    public void wxNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (super.notify(request, response)) {//回调成功
            response.setStatus(HttpServletResponse.SC_OK);
            out.write("success");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("fail");
        }
        out.flush();
    }
}
