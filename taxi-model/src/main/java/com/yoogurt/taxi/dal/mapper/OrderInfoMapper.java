package com.yoogurt.taxi.dal.mapper;

import com.github.pagehelper.Page;
import com.yoogurt.taxi.dal.beans.OrderInfo;
import com.yoogurt.taxi.dal.model.order.OrderModel;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;

public interface OrderInfoMapper extends Mapper<OrderInfo> {

    Page<OrderModel> getOrderList(@Param("orderId") Long orderId, @Param("phone") String phone,
                                  @Param("driverName") String driverName, @Param("status") Integer status,
                                  @Param("startTime") Date startTime, @Param("endTime") Date endTime);
}