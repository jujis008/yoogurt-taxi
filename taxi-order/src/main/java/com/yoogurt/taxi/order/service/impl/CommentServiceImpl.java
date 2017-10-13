package com.yoogurt.taxi.order.service.impl;

import com.google.common.base.Splitter;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.OrderCommentInfo;
import com.yoogurt.taxi.dal.beans.OrderInfo;
import com.yoogurt.taxi.dal.condition.order.CommentListCondition;
import com.yoogurt.taxi.dal.enums.OrderStatus;
import com.yoogurt.taxi.dal.enums.UserType;
import com.yoogurt.taxi.order.dao.CommentDao;
import com.yoogurt.taxi.order.form.CommentForm;
import com.yoogurt.taxi.order.service.CommentService;
import com.yoogurt.taxi.order.service.OrderInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private OrderInfoService orderInfoService;

    @Override
    public ResponseObj doComment(CommentForm commentForm) {

        Long orderId = commentForm.getOrderId();
        //检查订单是否存在
        OrderInfo orderInfo = orderInfoService.getOrderInfo(orderId, commentForm.getUserId());
        if(orderInfo == null) return ResponseObj.fail(StatusCode.BIZ_FAILED, "订单不存在");
        if (!OrderStatus.FINISH.getCode().equals(orderInfo.getStatus())) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "请结束订单后再提交评价");
        }
        //不能对自己评价
        if (!isAllowed(orderInfo, commentForm)) {
            return ResponseObj.fail(StatusCode.BIZ_FAILED, "评论对象有误");
        }
        //检查该用户有没有提交评论
        CommentListCondition condition = new CommentListCondition();
        condition.setOrderId(commentForm.getOrderId());
        condition.setUserId(commentForm.getUserId());
        if(getComments(condition).size() > 0) return ResponseObj.fail(StatusCode.BIZ_FAILED, "该订单已评论");
        //构造评论信息
        OrderCommentInfo comment = new OrderCommentInfo();
        BeanUtils.copyProperties(commentForm, comment);
        comment.setTagId(StringUtils.join(commentForm.getTagId(), ","));
        comment.setTagName(StringUtils.join(commentForm.getTagName(), ","));
        return commentDao.insert(comment) == 1 ? ResponseObj.success(comment) : ResponseObj.fail();
    }

    @Override
    public List<OrderCommentInfo> getComments(CommentListCondition condition) {

        Example ex = new Example(OrderCommentInfo.class);
        Example.Criteria criteria = ex.createCriteria();
        if (condition.getUserId() != null) {
            criteria.andEqualTo("userId", condition.getUserId());
        }
        if (condition.getOrderId() != null) {
            criteria.andEqualTo("orderId", condition.getOrderId());
        }
        if (condition.getDriverId() != null) {
            criteria.andEqualTo("driverId", condition.getDriverId());
        }
        criteria.andEqualTo("isDeleted", Boolean.FALSE);
        return commentDao.selectByExample(ex);
    }

    @Override
    public int removeComments(String commentIds) {
        if(StringUtils.isBlank(commentIds)) return 0;
        Example ex = new Example(OrderCommentInfo.class);
        ex.createCriteria().andIn("id", Splitter.on(",").split(commentIds));
        OrderCommentInfo probe = new OrderCommentInfo();
        probe.setIsDeleted(Boolean.TRUE);
        return commentDao.updateByExampleSelective(probe, ex);
    }


    private boolean isAllowed(OrderInfo orderInfo, CommentForm commentForm) {
        Long driverId = commentForm.getDriverId();
        Integer userType = commentForm.getUserType();
        return UserType.USER_APP_OFFICE.getCode().equals(userType) && orderInfo.getAgentDriverId().equals(driverId)
                || UserType.USER_APP_AGENT.getCode().equals(userType) && orderInfo.getOfficialDriverId().equals(driverId);
    }
}
