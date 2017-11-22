package com.yoogurt.taxi.order.dao.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yoogurt.taxi.common.dao.impl.BaseDao;
import com.yoogurt.taxi.dal.beans.RentInfo;
import com.yoogurt.taxi.dal.condition.order.RentListCondition;
import com.yoogurt.taxi.dal.condition.order.RentPoiCondition;
import com.yoogurt.taxi.dal.mapper.RentInfoMapper;
import com.yoogurt.taxi.dal.model.order.RentInfoModel;
import com.yoogurt.taxi.order.dao.RentDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RentDaoImpl extends BaseDao<RentInfoMapper, RentInfo> implements RentDao {

    @Autowired
    private RentInfoMapper rentInfoMapper;

    @Override
    public List<RentInfoModel> getRentList(RentPoiCondition condition) {
        return rentInfoMapper.getRentList(condition.getUserId(), condition.getUserType(), condition.getStatus(),
                condition.getMaxLng(), condition.getMinLng(),
                condition.getMaxLat(), condition.getMinLat(),
                condition.getStartTime(), condition.getEndTime(), condition.likes());
    }

    @Override
    public Page<RentInfoModel> getRentListByPage(RentListCondition condition) {
        String orderBy = "";
        if (StringUtils.isNotBlank(condition.getSortName())) {
            orderBy += condition.getSortName();
        }
        orderBy += " ";
        if(StringUtils.isNotBlank(condition.getSortOrder())) {
            orderBy += condition.getSortOrder();
        }
        //没有传入排序字段
        if (StringUtils.isBlank(orderBy)) {
            //默认按发布时间倒序排列
            orderBy += "r.gmt_create DESC";
        }
        PageHelper.startPage(condition.getPageNum(), condition.getPageSize(), orderBy);
        return rentInfoMapper.getRentListByPage(condition.getUserId(), condition.getUserType(), condition.getStatus(),
                condition.getMaxLng(), condition.getMinLng(),
                condition.getMaxLat(), condition.getMinLat(),
                condition.getStartTime(), condition.getEndTime(), condition.likes(),
                condition.getSortName(), condition.getSortOrder());
    }
}

