package com.yoogurt.taxi.notification.controller.mobile;

import com.yoogurt.taxi.common.controller.BaseController;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.Message;
import com.yoogurt.taxi.dal.condition.notification.MessageCondition;
import com.yoogurt.taxi.notification.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/mobile/notification")
public class MessageMobileController extends BaseController {

    @Autowired
    private MessageService messageService;

    @RequestMapping(value = "/messages", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj getMessages(MessageCondition condition) {
        condition.setUserId(super.getUserId());
        List<Message> messages = messageService.getMessages(condition);
        return ResponseObj.success(messages);
    }

}
