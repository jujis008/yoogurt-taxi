package com.yoogurt.taxi.finance.controller.mobile;

import com.yoogurt.taxi.dal.doc.finance.Event;
import com.yoogurt.taxi.finance.service.AlipayService;
import com.yoogurt.taxi.finance.service.NotifyService;
import com.yoogurt.taxi.finance.task.EventTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * 程序执行完后必须打印输出“success”（不包含引号）。
 * 如果商户反馈给支付宝的字符不是success这7个字符，支付宝服务器会不断重发通知，直到超过24小时22分钟。
 * 一般情况下，25小时以内完成8次通知（通知的间隔频率一般是：4m,10m,10m,1h,2h,6h,15h）。
 */
@RestController
@RequestMapping("/webhooks/finance")
public class AlipayNotifyController {

    @Autowired
    private AlipayService alipayService;

    @Autowired
    private NotifyService alipayNotifyService;

    /**
     * 支付宝App支付成功的回调接口
     *
     * @param request  回调请求对象
     * @param response 响应对象
     */
    @RequestMapping(value = "/i/alipay", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    public void notify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        Map<String, Object> parameterMap = alipayService.parameterResolve(request);
        Event event = alipayNotifyService.eventParse(parameterMap);
        //回调任务构造失败
        if (event == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("fail");
            return;
        }
        //回调任务提交失败
        if (alipayNotifyService.submit(event) == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("fail");
            return;
        }
        response.setStatus(HttpServletResponse.SC_OK);
        out.write("success");
        out.flush();
    }

}
