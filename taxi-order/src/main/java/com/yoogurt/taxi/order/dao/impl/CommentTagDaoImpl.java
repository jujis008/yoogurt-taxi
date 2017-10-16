package com.yoogurt.taxi.order.dao.impl;

import com.yoogurt.taxi.common.dao.impl.BaseDao;
import com.yoogurt.taxi.dal.beans.CommentTag;
import com.yoogurt.taxi.dal.mapper.CommentTagMapper;
import com.yoogurt.taxi.order.dao.CommentTagDao;
import org.springframework.stereotype.Repository;

@Repository
public class CommentTagDaoImpl extends BaseDao<CommentTagMapper, CommentTag> implements CommentTagDao {
}
