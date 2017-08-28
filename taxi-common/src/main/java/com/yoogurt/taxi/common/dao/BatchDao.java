package com.yoogurt.taxi.common.dao;

import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

/**
 * Description:
 * 封装一些数据库的批量操作。
 * @see com.yoogurt.taxi.common.dao.BaseDao
 * @Author Eric Lau
 * @Date 2017/8/28.
 */
public abstract class BatchDao<M extends MySqlMapper<T>, T> extends BaseDao {

    @Autowired
    private M mapper;

    public int insertList(List<T> dataList) { return mapper.insertList(dataList); }


}
