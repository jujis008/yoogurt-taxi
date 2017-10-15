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
import com.yoogurt.taxi.order.form.OrderStatisticForm;
import com.yoogurt.taxi.order.service.CommentService;
import com.yoogurt.taxi.order.service.CommentTagStatisticService;
import com.yoogurt.taxi.order.service.OrderInfoService;
import com.yoogurt.taxi.order.service.OrderStatisticService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private CommentTagStatisticService statisticService;

    @Autowired
    private OrderStatisticService orderStatisticService;

    @Transactional
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
        //获取评价对象id
        Long toCommentUseId = UserType.USER_APP_OFFICE.getCode().equals(commentForm.getUserType()) ? orderInfo.getAgentUserId() : orderInfo.getOfficialUserId();
        //检查该用户有没有提交评论
        CommentListCondition condition = new CommentListCondition();
        condition.setOrderId(commentForm.getOrderId());
        condition.setFromUserId(commentForm.getUserId());
        condition.setToUserId(toCommentUseId);
        if(getComments(condition).size() > 0) return ResponseObj.fail(StatusCode.BIZ_FAILED, "该订单已评论");
        //构造评论信息
        OrderCommentInfo comment = new OrderCommentInfo();
        BeanUtils.copyProperties(commentForm, comment);
        comment.setFromUserId(commentForm.getUserId());
        comment.setToUserId(toCommentUseId);
        comment.setTagId(StringUtils.join(commentForm.getTagId(), ","));
        comment.setTagName(StringUtils.join(commentForm.getTagName(), ","));
        if (commentDao.insert(comment) == 1) {
            //记录标签使用情况
            commentStatisticRecord(toCommentUseId, commentForm.getTagId(), commentForm.getTagName());
            //记录平均分
            orderStatisticRecord(toCommentUseId, commentForm.getScore());
            return ResponseObj.success(comment);
        }
        return ResponseObj.fail();
    }

    @Override
    public List<OrderCommentInfo> getComments(CommentListCondition condition) {

        Example ex = new Example(OrderCommentInfo.class);
        Example.Criteria criteria = ex.createCriteria();
        if (condition.getFromUserId() != null) {
            criteria.andEqualTo("fromUserId", condition.getFromUserId());
        }
        if (condition.getToUserId() != null) {
            criteria.andEqualTo("toUserId", condition.getToUserId());
        }
        if (condition.getOrderId() != null) {
            criteria.andEqualTo("orderId", condition.getOrderId());
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

    @Override
    public Double getAvgScore(Long userId) {
        if(userId == null || userId <= 0) return 0.00;
        return commentDao.getAvgScore(userId);
    }

    /**
     * 获取被评论者当前的平均分
     * @param userId 被评论者的id
     * @return 平均分
     */
    private double calculateAvgScore(Long userId) {
        CommentListCondition condition = new CommentListCondition();
        condition.setToUserId(userId);
        List<OrderCommentInfo> comments = getComments(condition);
        if(CollectionUtils.isEmpty(comments)) return 0.00;
        double totalScore = 0.00;
        for (OrderCommentInfo comment : comments) {
            totalScore += comment.getScore();
        }
        return totalScore / comments.size();
    }

    private boolean isAllowed(OrderInfo orderInfo, CommentForm commentForm) {
        Long fromUserId = commentForm.getUserId();
        Integer userType = commentForm.getUserType();
        return UserType.USER_APP_OFFICE.getCode().equals(userType) && orderInfo.getOfficialUserId().equals(fromUserId)
                || UserType.USER_APP_AGENT.getCode().equals(userType) && orderInfo.getAgentUserId().equals(fromUserId);
    }

    private void commentStatisticRecord(Long userId, Long[] tagIds, String[] tagNames) {
        try {
            statisticService.record(userId, tagIds, tagNames);
        } catch (Exception e){
            log.error("评价标签统计异常, {}", e);
        }
    }

    private void orderStatisticRecord(Long userId, Integer score) {
        try {
            OrderStatisticForm form = OrderStatisticForm.builder()
                    .userId(userId)
                    .score(new BigDecimal(calculateAvgScore(userId)))
                    .build();
            orderStatisticService.record(form);
        } catch (Exception e) {
            log.error("平均分统计异常, {}", e);
        }
    }
}
