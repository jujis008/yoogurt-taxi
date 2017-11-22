package com.yoogurt.taxi.system.dao.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yoogurt.taxi.common.dao.impl.BaseDao;
import com.yoogurt.taxi.common.factory.WebPagerFactory;
import com.yoogurt.taxi.common.pager.BasePager;
import com.yoogurt.taxi.dal.beans.FeedbackRecord;
import com.yoogurt.taxi.dal.condition.system.FeedbackRecordCondition;
import com.yoogurt.taxi.dal.mapper.FeedbackRecordMapper;
import com.yoogurt.taxi.system.dao.FeedbackRecordDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;

@Repository
public class FeedbackRecordDaoImpl extends BaseDao<FeedbackRecordMapper, FeedbackRecord> implements FeedbackRecordDao {
    @Autowired
    private FeedbackRecordMapper feedbackRecordMapper;
    @Autowired
    private WebPagerFactory webPagerFactory;


    @Override
    public BasePager<FeedbackRecord> getWebList(FeedbackRecordCondition condition) {
        PageHelper.startPage(condition.getPageNum(),condition.getPageSize());
        Example example = new Example(FeedbackRecord.class);
        example.setOrderByClause(" gmt_create desc");
        Example.Criteria criteria = example.createCriteria().andEqualTo("isDeleted",Boolean.FALSE);
        if (condition.getFeedbackType() != null){
            criteria.andEqualTo("feedbackType",condition.getFeedbackType());
        }
        if (StringUtils.isNotBlank(condition.getUsername())) {
            criteria.andEqualTo("username",condition.getUsername());
        }
        Page<FeedbackRecord> recordList = (Page<FeedbackRecord>) feedbackRecordMapper.selectByExample(example);
        return webPagerFactory.generatePager(recordList);
    }
}
