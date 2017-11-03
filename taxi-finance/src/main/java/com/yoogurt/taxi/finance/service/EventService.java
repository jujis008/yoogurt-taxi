package com.yoogurt.taxi.finance.service;

import com.yoogurt.taxi.dal.bo.Notify;
import com.yoogurt.taxi.dal.doc.finance.Event;

public interface EventService<T extends Notify> extends EventTaskService {

    Event<T> getEvent(String eventId);

    Event<T> addEvent(Event<T> event);

    Event<T> saveEvent(Event<T> event);

    boolean deleteEvent(String eventId);
}
