package com.yoogurt.taxi.order.service;

import com.yoogurt.taxi.dal.beans.CommentTagStatistic;
import com.yoogurt.taxi.order.form.CommentForm;

import java.util.List;

public interface CommentTagStatisticService {

    /**
     * 记录本次评价标签的使用情况
     * @param commentForm 评价表单信息
     * @return 标签使用统计信息
     */
    void record(CommentForm commentForm);

    /**
     * 获取司机评价标签统计信息
     * @param userId 用户id
     * @return 标签使用统计信息
     */
    List<CommentTagStatistic> getStatistic(Long userId);
}
