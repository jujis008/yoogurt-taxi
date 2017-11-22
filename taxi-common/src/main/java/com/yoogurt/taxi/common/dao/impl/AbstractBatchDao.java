package com.yoogurt.taxi.common.dao.impl;

import com.yoogurt.taxi.common.dao.IBatchDao;
import com.yoogurt.taxi.common.mapper.SuperMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Description:
 * 封装一些数据库的批量操作。
 * @see BaseDao
 * @author Eric Lau
 * @date 2017/8/28.
 */
public abstract class AbstractBatchDao<M extends SuperMapper<T>, T> extends BaseDao<M, T> implements IBatchDao<M, T> {

    @Autowired
    private M mapper;

    @Override
    public int insertList(List<T> dataList) { return mapper.insertList(dataList); }

}
