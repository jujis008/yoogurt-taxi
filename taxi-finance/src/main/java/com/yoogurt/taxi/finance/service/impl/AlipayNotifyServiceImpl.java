package com.yoogurt.taxi.finance.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yoogurt.taxi.common.bo.Money;
import com.yoogurt.taxi.common.utils.RandomUtils;
import com.yoogurt.taxi.dal.bo.AlipayNotify;
import com.yoogurt.taxi.dal.bo.Notify;
import com.yoogurt.taxi.dal.doc.finance.Event;
import com.yoogurt.taxi.dal.enums.EventType;
import com.yoogurt.taxi.dal.enums.PayChannel;
import com.yoogurt.taxi.pay.service.impl.NotifyServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service("alipayNotifyService")
public class AlipayNotifyServiceImpl extends NotifyServiceImpl {

    /**
     * 解析回调请求，组装EventTask
     *
     * @param parameterMap 回调请求
     * @return EventTask
     */
    @Override
    public Event<? extends Notify> eventParse(Map<String, Object> parameterMap) {
        if (parameterMap == null || parameterMap.isEmpty()) return null;
        //生成一个eventId
        String eventId = "event_" + RandomUtils.getPrimaryKey();
        //构造一个 Notify
        AlipayNotify notify = new AlipayNotify();
        try {
            //将Map中的参数注入到
            BeanUtils.populate(notify, parameterMap);
            notify.setChannel(PayChannel.ALIPAY.getName());
            notify.setAmount(new Money(parameterMap.get("total_amount").toString()).getCent());
            notify.setNotifyTimestamp(DateTime.parse(parameterMap.get("notify_time").toString(), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).getMillis());
            notify.setPaidTimestamp(DateTime.parse(notify.getGmtPayment(), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).getMillis());
            //回传参数
            if (parameterMap.get("passback_params") != null) {
                ObjectMapper mapper = new ObjectMapper();
                notify.setMetadata(mapper.readValue(parameterMap.get("passback_params").toString(), Map.class));
            }
            Event<AlipayNotify> event = new Event<>(eventId, notify);
            event.setEventType(EventType.PAY_SUCCEEDED.getCode());
            return event;
        } catch (Exception e) {
            log.error("回调参数解析发生异常, {}", e);
        }
        return null;
    }
}
