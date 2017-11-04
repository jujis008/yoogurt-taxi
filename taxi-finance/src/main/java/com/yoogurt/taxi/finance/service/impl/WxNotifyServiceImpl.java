package com.yoogurt.taxi.finance.service.impl;

import com.yoogurt.taxi.common.utils.RandomUtils;
import com.yoogurt.taxi.dal.bo.AlipayNotify;
import com.yoogurt.taxi.dal.bo.WxNotify;
import com.yoogurt.taxi.dal.doc.finance.Event;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service("wxNotifyService")
public class WxNotifyServiceImpl extends NotifyServiceImpl {

    /**
     * 解析回调请求，组装EventTask
     *
     * @param parameterMap 回调请求
     * @return EventTask
     */
    @Override
    public Event eventParse(Map<String, Object> parameterMap) {
        if (parameterMap == null || parameterMap.isEmpty()) return null;
        //生成一个eventId
        String eventId = "event_" + RandomUtils.getPrimaryKey();
        //构造一个 Notify
        WxNotify notify = new WxNotify();
        //构造一个 Event
        Event<WxNotify> event = new Event<>(eventId, notify);
        try {
            //将Map中的参数注入到
            BeanUtils.populate(notify, parameterMap);
            return event;
        } catch (Exception e) {
            log.error("回调参数解析发生异常, {}", e);
        }
        return null;
    }
}
