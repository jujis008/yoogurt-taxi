package com.yoogurt.taxi.order.dao.impl;

import com.yoogurt.taxi.common.dao.impl.BaseDao;
import com.yoogurt.taxi.dal.beans.OrderCommentInfo;
import com.yoogurt.taxi.dal.mapper.OrderCommentInfoMapper;
import com.yoogurt.taxi.order.dao.CommentDao;
import org.springframework.stereotype.Repository;

@Repository
public class CommentDaoImpl extends BaseDao<OrderCommentInfoMapper, OrderCommentInfo> implements CommentDao {
}
