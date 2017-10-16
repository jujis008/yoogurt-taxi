package com.yoogurt.taxi.order.service.impl;

import com.yoogurt.taxi.dal.beans.CommentTag;
import com.yoogurt.taxi.order.dao.CommentTagDao;
import com.yoogurt.taxi.order.service.CommentTagService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class CommentTagServiceImpl implements CommentTagService {

    @Autowired
    private CommentTagDao commentTagDao;

    @Override
    public List<CommentTag> getTags() {
        Example ex = new Example(CommentTag.class);
        ex.createCriteria().andEqualTo("isDeleted", Boolean.FALSE);
        return commentTagDao.selectByExample(ex);
    }

    @Override
    public CommentTag addCommentTag(CommentTag tag) {
        if(tag == null) return null;
        if(commentTagDao.insertSelective(tag) == 1) return tag;
        return null;
    }

    @Override
    public CommentTag updateCommentTag(CommentTag tag) {
        if(tag == null) return null;
        if(commentTagDao.updateByIdSelective(tag) == 1) return tag;
        return null;
    }

    @Override
    public int deleteCommentTag(List<Long> tagIds) {
        if(CollectionUtils.isEmpty(tagIds)) return 0;
        Example ex = new Example(CommentTag.class);
        ex.createCriteria().andEqualTo("isDeleted", Boolean.FALSE).andIn("id", tagIds);
        CommentTag probe = new CommentTag();
        probe.setIsDeleted(Boolean.TRUE);
        return commentTagDao.updateByExampleSelective(probe, ex);
    }
}
