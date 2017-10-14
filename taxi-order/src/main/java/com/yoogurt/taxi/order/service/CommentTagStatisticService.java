package com.yoogurt.taxi.order.service;

import com.yoogurt.taxi.dal.beans.CommentTagStatistic;

import java.util.List;

public interface CommentTagStatisticService {

    /**
     * 记录本次评价标签的使用情况
     *
     * @param userId 被评价的用户id
     * @param tagIds 一组评价标签id
     * @param tagNames 一组评价标签名称
     */
    void record(Long userId, Long[] tagIds, String[] tagNames);

    /**
     * 获取司机评价标签统计信息
     * @param userId 用户id
     * @return 标签使用统计信息
     */
    List<CommentTagStatistic> getStatistic(Long userId);
}
