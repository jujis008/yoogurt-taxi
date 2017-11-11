package com.yoogurt.taxi.order.service.impl;

import com.yoogurt.taxi.dal.beans.CommentTag;
import com.yoogurt.taxi.dal.beans.CommentTagStatistic;
import com.yoogurt.taxi.order.dao.CommentTagDao;
import com.yoogurt.taxi.order.dao.CommentTagStatisticDao;
import com.yoogurt.taxi.order.service.CommentTagStatisticService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentTagStatisticServiceImpl implements CommentTagStatisticService {

    @Autowired
    private CommentTagStatisticDao statisticDao;

    @Autowired
    private CommentTagDao commentTagDao;

    /**
     * 记录本次评价标签的使用情况
     *
     * @param userId 被评价的用户id
     * @param tagIds 一组评价标签id
     */
    @Override
    public void record(String userId, Long[] tagIds) {
        List<CommentTagStatistic> statistics = buildStatistic(userId, tagIds);
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
    public List<CommentTagStatistic> getStatistic(String userId) {
        if (StringUtils.isBlank(userId)) return null;
        Example ex = new Example(CommentTagStatistic.class);
        ex.createCriteria().andEqualTo("isDeleted", Boolean.FALSE).andEqualTo("userId", userId);
        return statisticDao.selectByExample(ex);
    }

    private List<CommentTagStatistic> buildStatistic(String userId, Long[] tagIds) {
        List<CommentTagStatistic> statistics = new ArrayList<>();
        if (userId == null || tagIds == null || tagIds.length == 0) return statistics;
        //长度不对应
        for (Long tagId : tagIds) {

            CommentTagStatistic statistic = new CommentTagStatistic(userId);
            CommentTag commentTag = commentTagDao.selectById(tagId);
            if (commentTag == null) continue;
            statistic.setTagId(tagId);
            BeanUtils.copyProperties(commentTag, statistic);
            statistic.setCounter(1);
            statistics.add(statistic);
        }
        return statistics;
    }

    private CommentTagStatistic addStatistic(CommentTagStatistic statistic) {
        if (statistic == null) return null;
        if (statisticDao.insertSelective(statistic) == 1) {
            return statistic;
        }
        return null;
    }
}
