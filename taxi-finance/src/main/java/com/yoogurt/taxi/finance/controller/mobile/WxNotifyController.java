package com.yoogurt.taxi.finance.controller.mobile;

import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.bo.Notify;
import com.yoogurt.taxi.dal.bo.WxNotify;
import com.yoogurt.taxi.dal.doc.finance.Event;
import com.yoogurt.taxi.finance.service.NotifyService;
import com.yoogurt.taxi.finance.service.PayChannelService;
import com.yoogurt.taxi.finance.task.EventTask;
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
 * 对后台通知交互时，如果微信收到商户的应答不是成功或超时，微信认为通知失败，
 * 微信会通过一定的策略定期重新发起通知，尽可能提高通知的成功率，但微信不保证通知最终能成功。
 * （通知频率为15/15/30/180/1800/1800/1800/1800/3600，单位：秒）
 * 注意：同样的通知可能会多次发送给商户系统。商户系统必须能够正确处理重复的通知。
 */
@RestController
@RequestMapping("/webhooks/finance")
public class WxNotifyController {

    @Autowired
    private NotifyService wxNotifyService;

    @Autowired
    private PayChannelService wxPayService;

    @RequestMapping(value = "/i/wx", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    public void wxNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        //参数解析&映射
        Map<String, Object> parameterMap = wxPayService.parameterResolve(request, new WxNotify().attributeMap());
        //event对象解析
        Event<WxNotify> event = (Event<WxNotify>) wxNotifyService.eventParse(parameterMap);

        if (event != null) {
            EventTask eventTask = wxNotifyService.submit(event);
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
    @RequestMapping(value = "/wx/result/{taskId}", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj queryResult(@PathVariable(name = "taskId") String taskId) {
        Event<Notify> event = wxNotifyService.queryResult(taskId);
        return ResponseObj.success(event);
    }

    /**
     * 获取回调事件对象
     *
     * @param eventId 事件对象ID
     * @return ResponseObj
     */
    @RequestMapping(value = "/wx/event/{eventId}", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj getEventTask(@PathVariable(name = "eventId") String eventId) {

        Event<Notify> event = wxNotifyService.queryResult(eventId);
        return ResponseObj.success(event);
    }
}
