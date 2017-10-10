package com.yoogurt.taxi.common.dao;

import com.yoogurt.taxi.common.mapper.SuperMapper;

import java.util.List;

public interface IBatchDao<M extends SuperMapper<T>, T> extends IDao<M, T>{

    int insertList(List<T> dataList);
}
