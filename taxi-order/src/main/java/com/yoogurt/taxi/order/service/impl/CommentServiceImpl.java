package com.yoogurt.taxi.order.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.yoogurt.taxi.dal.beans.OrderCommentInfo;
import com.yoogurt.taxi.dal.beans.OrderInfo;
import com.yoogurt.taxi.dal.condition.order.CommentListCondition;
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
    public OrderCommentInfo doComment(CommentForm commentForm) {

        Long orderId = commentForm.getOrderId();
        OrderInfo orderInfo = orderInfoService.getOrderInfo(orderId);
        if(orderInfo.getIsCommented()) return null;
        OrderCommentInfo comment = new OrderCommentInfo();
        BeanUtils.copyProperties(commentForm, comment);
        comment.setTagId(StringUtils.join(commentForm.getTagId(), ","));
        comment.setTagName(StringUtils.join(commentForm.getTagName(), ","));
        if (commentDao.insert(comment) == 1) {
            orderInfo.setIsCommented(true);
            orderInfoService.saveOrderInfo(orderInfo, false);
            return comment;
        }
        return null;
    }

    @Override
    public List<OrderCommentInfo> getComments(CommentListCondition condition) {

        List<OrderCommentInfo> commentList = Lists.newArrayList();
        Example ex = new Example(OrderCommentInfo.class);
        Example.Criteria criteria = ex.createCriteria();
        if (condition.getDriverId() != null) {
            criteria.andEqualTo("driverId", condition.getDriverId());
        } else if (condition.getOrderId() != null) {
            criteria.andEqualTo("orderId", condition.getOrderId());
        } else { //没有查询条件，返回空列表
            return commentList;
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

}
