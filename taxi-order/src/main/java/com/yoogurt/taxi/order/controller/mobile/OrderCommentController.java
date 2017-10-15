package com.yoogurt.taxi.order.controller.mobile;

import com.yoogurt.taxi.common.bo.SessionUser;
import com.yoogurt.taxi.common.controller.BaseController;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.OrderCommentInfo;
import com.yoogurt.taxi.dal.condition.order.CommentListCondition;
import com.yoogurt.taxi.order.form.CommentForm;
import com.yoogurt.taxi.order.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/mobile/order")
public class OrderCommentController extends BaseController {

    @Autowired
    private CommentService commentService;

    @RequestMapping(value = "/comments", method = RequestMethod.GET, produces = {"application/json;charset=utf-8"})
    public ResponseObj getComments(CommentListCondition condition) {
        return ResponseObj.success(commentService.getComments(condition));
    }

    @RequestMapping(value = "/comment", method = RequestMethod.POST, produces = {"application/json;charset=utf-8"})
    public ResponseObj doComment(@Valid @RequestBody CommentForm form, BindingResult result) {
        if(result.hasErrors()) {
            return ResponseObj.fail(StatusCode.FORM_INVALID, result.getAllErrors().get(0).getDefaultMessage());
        }
        SessionUser user = super.getUser();
        form.setUserId(user.getUserId());
        form.setUserType(user.getType());

        return commentService.doComment(form);
    }
}
