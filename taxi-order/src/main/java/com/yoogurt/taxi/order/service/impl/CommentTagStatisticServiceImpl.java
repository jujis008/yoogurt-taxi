package com.yoogurt.taxi.order.service.impl;

import com.yoogurt.taxi.dal.beans.CommentTagStatistic;
import com.yoogurt.taxi.order.dao.CommentTagStatisticDao;
import com.yoogurt.taxi.order.form.CommentForm;
import com.yoogurt.taxi.order.service.CommentTagStatisticService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentTagStatisticServiceImpl implements CommentTagStatisticService {

    @Autowired
    private CommentTagStatisticDao statisticDao;

    /**
     * 记录本次评价标签的使用情况
     *
     * @param commentForm 评价表单信息
     */
    @Override
    public void record(CommentForm commentForm) {
        List<CommentTagStatistic> statistics = buildStatistic(commentForm.getUserId(), commentForm.getTagId(), commentForm.getTagName());
        if (CollectionUtils.isNotEmpty(statistics)) {
            statistics.forEach(statistic -> statisticDao.saveStatistic(statistic));
        }
    }

    /**
     * 获取司机评价标签统计信息
     *
     * @param userId 用户id
     * @return 标签使用统计信息
     */
    @Override
    public List<CommentTagStatistic> getStatistic(Long userId) {
        if(userId == null || userId <= 0) return null;
        Example ex = new Example(CommentTagStatistic.class);
        ex.createCriteria().andEqualTo("isDeleted", Boolean.FALSE).andEqualTo("userId", userId);
        return statisticDao.selectByExample(ex);
    }

    private List<CommentTagStatistic> buildStatistic(Long userId, String[] tagIds, String[] tagNames) {
        List<CommentTagStatistic> statistics = new ArrayList<>();
        if(userId == null || tagIds == null || tagIds.length == 0 || tagNames == null || tagNames.length == 0) return statistics;
        //长度不对应
        if(tagIds.length != tagNames.length) return statistics;
        for (int i = 0, len = tagIds.length; i < len; i++) {

            CommentTagStatistic statistic = new CommentTagStatistic(userId);
            statistic.setTagId(Long.valueOf(tagIds[i]));
            statistic.setTagName(tagNames[i]);
            statistic.setCounter(1);
            statistics.add(statistic);
        }
        return statistics;
    }

    private CommentTagStatistic addStatistic(CommentTagStatistic statistic) {
        if(statistic == null) return null;
        if (statisticDao.insertSelective(statistic) == 1) {
            return statistic;
        }
        return null;
    }
}
