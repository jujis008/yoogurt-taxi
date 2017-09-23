package com.yoogurt.taxi.common.dao;

import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

public interface IBatchDao<M extends MySqlMapper<T>, T> extends IDao {

    int insertList(List<T> dataList);
}
