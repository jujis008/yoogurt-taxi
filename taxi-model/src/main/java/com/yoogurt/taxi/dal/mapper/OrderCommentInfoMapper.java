package com.yoogurt.taxi.dal.mapper;

import com.yoogurt.taxi.dal.beans.OrderCommentInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface OrderCommentInfoMapper extends Mapper<OrderCommentInfo> {

    Double getAvgScore(@Param("userId") String uesrId);
}