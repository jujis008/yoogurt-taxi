package com.yoogurt.taxi.system.controller;

import com.yoogurt.taxi.common.controller.BaseController;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.FeedbackRecord;
import com.yoogurt.taxi.dal.beans.FeedbackType;
import com.yoogurt.taxi.system.form.FeedbackForm;
import com.yoogurt.taxi.system.service.FeedBackRecordService;
import com.yoogurt.taxi.system.service.FeedbackTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/mobile/system/feedback")
public class FeedbackController extends BaseController{
    @Autowired
    private FeedbackTypeService feedbackTypeService;
    @Autowired
    private FeedBackRecordService feedBackRecordService;

    @RequestMapping(value = "/type/list",method = RequestMethod.GET,produces = {"application/json;charset=utf-8"})
    public ResponseObj getFeedbackType() {
        List<FeedbackType> list = feedbackTypeService.getList();
        return ResponseObj.success(list);
    }

    @RequestMapping(value = "/content",method = RequestMethod.POST,produces = {"application/json;charset=utf-8"})
    public ResponseObj commitFeedback(@RequestBody @Valid FeedbackForm form, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID,result.getAllErrors().get(0).getDefaultMessage());
        }
        FeedbackType one = feedbackTypeService.getOne(form.getFeedbackType());
        if (one == null) {
            return ResponseObj.fail(StatusCode.FORM_INVALID,"错误的反馈类型");
        }
        FeedbackRecord feedbackRecord = new FeedbackRecord();
        feedbackRecord.setFeedbackContent(form.getContent());
        feedbackRecord.setFeedbackType(form.getFeedbackType());
        feedbackRecord.setIsHandle(Boolean.FALSE);
        feedbackRecord.setUserId(getUserId());
        feedbackRecord.setUsername(getUserName());
        feedbackRecord.setPhoneModel(form.getPhoneModel());
        feedbackRecord.setSystemVersion(form.getSystemVersion());
        feedbackRecord.setAppVersion(form.getAppVersion());
        feedBackRecordService.insert(feedbackRecord);
        return ResponseObj.success();
    }
}
