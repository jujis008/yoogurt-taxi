package com.yoogurt.taxi.order.service;


import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.OrderCommentInfo;
import com.yoogurt.taxi.dal.condition.order.CommentListCondition;
import com.yoogurt.taxi.order.form.CommentForm;

import java.util.List;

public interface CommentService {

	ResponseObj doComment(CommentForm commentForm);

	List<OrderCommentInfo> getComments(CommentListCondition condition);

	int removeComments(String commentIds);

}
