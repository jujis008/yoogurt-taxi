package com.yoogurt.taxi.finance.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yoogurt.taxi.common.utils.RandomUtils;
import com.yoogurt.taxi.dal.bo.Notify;
import com.yoogurt.taxi.dal.bo.WxNotify;
import com.yoogurt.taxi.dal.doc.finance.Event;
import com.yoogurt.taxi.dal.enums.PayChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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
    public Event<? extends Notify> eventParse(Map<String, Object> parameterMap) {
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
            notify.setChannel(PayChannel.WX.getName());
            notify.setCharset("UTF-8");
            notify.setSignType("MD5");
            notify.setSign(parameterMap.get("sign").toString());
            notify.setAmount(Long.valueOf(parameterMap.get("total_fee").toString()));
            long timestamp = DateTime.parse(notify.getTimeEnd(), DateTimeFormat.forPattern("yyyyMMddHHmmss")).getMillis();
            notify.setNotifyTimestamp(timestamp);
            notify.setPaidTimestamp(timestamp);
            //回传参数
            if (parameterMap.get("attach") != null) {
                Map<String, Object> metadata = new HashMap<>();
                String orderNo = "";
                String attach = parameterMap.get("attach").toString();
                String[] extras = attach.split("&");
                for (String str : extras) {
                    String[] pairs = str.split("=");
                    if(pairs.length != 2) continue;
                    metadata.put(pairs[0], pairs[1]);
                    if ("orderId".equals(pairs[0])) { //解析attach数据中的订单id
                        orderNo = pairs[1];
                    }
                }
                notify.setOrderNo(orderNo);
                notify.setMetadata(metadata);
            }
            return event;
        } catch (Exception e) {
            log.error("回调参数解析发生异常, {}", e);
        }
        return null;
    }
}
