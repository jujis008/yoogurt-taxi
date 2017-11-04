package com.yoogurt.taxi.finance.controller.mobile;

import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.doc.finance.Event;
import com.yoogurt.taxi.finance.service.NotifyService;
import com.yoogurt.taxi.finance.service.PayChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public abstract class NotifyController<Channel extends PayChannelService, Notify extends NotifyService> {

    @Autowired
    private Channel channel;

    @Autowired
    private Notify notify;

    public boolean notify(HttpServletRequest request, HttpServletResponse response) {

        Map<String, Object> parameterMap = channel.parameterResolve(request);
        Event event = notify.eventParse(parameterMap);
        //回调任务构造失败
        if (event == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return false;
        }
        //回调任务提交失败
        if (notify.submit(event) == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return false;
        }
        return true;
    }

    /**
     * 获取回调任务执行结果
     *
     * @param taskId 发起支付任务的ID
     * @return ResponseObj
     */
    @RequestMapping(value = "/result/{taskId}", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj queryResult(@PathVariable(name = "taskId") String taskId) {
        Event<com.yoogurt.taxi.dal.bo.Notify> event = notify.queryResult(taskId);
        return ResponseObj.success(event);
    }

    /**
     * 获取回调事件对象
     *
     * @param eventId 事件对象ID
     * @return ResponseObj
     */
    @RequestMapping(value = "/event/{eventId}", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj getEventTask(@PathVariable(name = "eventId") String eventId) {

        Event<com.yoogurt.taxi.dal.bo.Notify> event = notify.queryResult(eventId);
        return ResponseObj.success(event);
    }
}
