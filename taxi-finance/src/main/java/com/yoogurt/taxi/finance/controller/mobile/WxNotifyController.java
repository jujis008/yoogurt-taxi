package com.yoogurt.taxi.finance.controller.mobile;

import com.yoogurt.taxi.dal.doc.finance.Event;
import com.yoogurt.taxi.finance.service.NotifyService;
import com.yoogurt.taxi.finance.service.WxPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@RestController
@RequestMapping("/webhooks/finance")
public class WxNotifyController {

    @Autowired
    private WxPayService wxPayService;

    @Autowired
    private NotifyService wxNotifyService;

    @RequestMapping(value = "/i/wx", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    public void wxNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        Map<String, Object> parameterMap = wxPayService.parameterResolve(request);
        Event event = wxNotifyService.eventParse(parameterMap);
        //回调任务构造失败
        if (event == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("fail");
            return;
        }
        //回调任务提交失败
        if (wxNotifyService.submit(event) == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("fail");
            return;
        }
        response.setStatus(HttpServletResponse.SC_OK);
        out.write("success");
        out.flush();
    }
}
