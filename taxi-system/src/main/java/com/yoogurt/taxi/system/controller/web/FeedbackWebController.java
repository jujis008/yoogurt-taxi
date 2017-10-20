package com.yoogurt.taxi.system.controller.web;

import com.yoogurt.taxi.common.controller.BaseController;
import com.yoogurt.taxi.common.pager.Pager;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.FeedbackRecord;
import com.yoogurt.taxi.dal.beans.FeedbackType;
import com.yoogurt.taxi.dal.condition.system.FeedbackRecordCondition;
import com.yoogurt.taxi.system.controller.form.FeedbackSelectiveForm;
import com.yoogurt.taxi.system.service.FeedBackRecordService;
import com.yoogurt.taxi.system.service.FeedbackTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("web/system/feedback")
public class FeedbackWebController extends BaseController{
    @Autowired
    private FeedbackTypeService feedbackTypeService;
    @Autowired
    private FeedBackRecordService feedBackRecordService;

    @RequestMapping(value = "feedbackType/list",method = RequestMethod.GET,produces = {"application/json;charset=utf-8"})
    public ResponseObj getFeedbackType() {
        List<FeedbackType> list = feedbackTypeService.getList();
        return ResponseObj.success(list);
    }
    @RequestMapping(value = "list",method = RequestMethod.GET,produces = {"application/json;charset=utf-8"})
    public ResponseObj getWebList(FeedbackRecordCondition condition) {
        Pager<FeedbackRecord> webList = feedBackRecordService.getWebList(condition);
        return ResponseObj.success(webList);
    }

    @RequestMapping(value = "info/id/{id}",method = RequestMethod.GET,produces = {"application/json;charset=utf-8"})
    public ResponseObj getOne(@PathVariable(name = "id") Long id) {
        FeedbackRecord one = feedBackRecordService.getOne(id);
        return ResponseObj.success(one);
    }

    @RequestMapping(value = "info",method = RequestMethod.PATCH,produces = {"application/json;charset=utf-8"})
    public ResponseObj handle(@RequestBody FeedbackSelectiveForm form) {
        FeedbackRecord record = new FeedbackRecord();
        record.setId(form.getId());
        record.setIsHandle(Boolean.TRUE);
        return ResponseObj.success(feedBackRecordService.update(record));
    }

}
