package com.yoogurt.taxi.finance.controller.mobile;

import com.yoogurt.taxi.common.controller.BaseController;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.bo.Notify;
import com.yoogurt.taxi.dal.bo.WxNotify;
import com.yoogurt.taxi.dal.doc.finance.Event;
import com.yoogurt.taxi.dal.doc.finance.EventTask;
import com.yoogurt.taxi.finance.service.NotifyService;
import com.yoogurt.taxi.finance.service.PayChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 对后台通知交互时，如果微信收到商户的应答不是成功或超时，微信认为通知失败，
 * 微信会通过一定的策略定期重新发起通知，尽可能提高通知的成功率，但微信不保证通知最终能成功。
 * （通知频率为15/15/30/180/1800/1800/1800/1800/3600，单位：秒）
 * 注意：同样的通知可能会多次发送给商户系统。商户系统必须能够正确处理重复的通知。
 */
@Slf4j
@RestController
@RequestMapping("/webhooks/finance")
public class WxNotifyController extends BaseController {

    @Autowired
    private NotifyService wxNotifyService;

    @Autowired
    private PayChannelService wxPayService;

    @RequestMapping(value = "/i/wx", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    public void wxNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        Map<String, Object> params = wxPayService.parameterResolve(request, null);
        //回调验签
        if (!wxPayService.signVerify(params, "MD5", "UTF-8")) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("非法的回调请求");
            return;
        }
        Map<String, Object> attributeMap = new WxNotify().attributeMap();
        Map<String, Object> parameterMap = new HashMap<>();
        Set<String> keySet = params.keySet();
        //参数解析&映射
        for (String key : keySet) {
            if (attributeMap.get(key) == null) {
                parameterMap.put(key, params.get(key));
            } else {
                parameterMap.put(attributeMap.get(key).toString(), params.get(key));
            }
        }
        //event对象解析
        Event<? extends Notify> event = wxNotifyService.eventParse(parameterMap);
        if (event != null) {
            log.info("[WxNotifyController]接收到微信回调：\n" + event.toString());
            EventTask eventTask = wxNotifyService.submit(event);
            if (eventTask != null) {//回调成功
                log.info("[WxNotifyController]微信回调任务提交成功：\n" + event.toString());
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
