package com.yoogurt.taxi.finance.controller.mobile;

import com.yoogurt.taxi.common.utils.RSA;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.bo.AlipayNotify;
import com.yoogurt.taxi.dal.bo.Notify;
import com.yoogurt.taxi.dal.doc.finance.Event;
import com.yoogurt.taxi.dal.doc.finance.EventTask;
import com.yoogurt.taxi.finance.service.NotifyService;
import com.yoogurt.taxi.finance.service.PayChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
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
    private NotifyService alipayNotifyService;

    @Autowired
    private PayChannelService alipayService;

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
        //回调验签
        if (!alipayService.signVerify(request, RSA.RSA2_ALGORITHMS, RSA.getDefaultCharset())) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("非法的回调请求");
            return;
        }
        //参数解析&映射
        Map<String, Object> parameterMap = alipayService.parameterResolve(request, new AlipayNotify().attributeMap());
        //event对象解析
        Event<AlipayNotify> event = (Event<AlipayNotify>) alipayNotifyService.eventParse(parameterMap);
        if (event != null) {

            EventTask eventTask = alipayNotifyService.submit(event);
            if (eventTask != null) {//回调成功
                response.setStatus(HttpServletResponse.SC_OK);
                out.write("success");
                return;
            }
        }
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        out.write("fail");
        out.flush();
    }

    /**
     * 获取回调任务执行结果
     *
     * @param taskId 发起支付任务的ID
     * @return ResponseObj
     */
    @RequestMapping(value = "/alipay/result/{taskId}", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj queryResult(@PathVariable(name = "taskId") String taskId) {
        Event<Notify> event = alipayNotifyService.queryResult(taskId);
        return ResponseObj.success(event);
    }

    /**
     * 获取回调事件对象
     *
     * @param eventId 事件对象ID
     * @return ResponseObj
     */
    @RequestMapping(value = "/alipay/event/{eventId}", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj getEventTask(@PathVariable(name = "eventId") String eventId) {

        Event<Notify> event = alipayNotifyService.queryResult(eventId);
        return ResponseObj.success(event);
    }

}
