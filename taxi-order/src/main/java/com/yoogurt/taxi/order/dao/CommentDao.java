package com.yoogurt.taxi.order.dao;

import com.yoogurt.taxi.common.dao.IDao;
import com.yoogurt.taxi.dal.beans.OrderCommentInfo;
import com.yoogurt.taxi.dal.mapper.OrderCommentInfoMapper;
import org.apache.ibatis.annotations.Param;

public interface CommentDao extends IDao<OrderCommentInfoMapper, OrderCommentInfo> {

    Double getAvgScore(@Param("userId") Long userId);
}
