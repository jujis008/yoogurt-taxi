package com.yoogurt.taxi.common.dao;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 数据访问层公共接口
 * @param <M>  Mapper类
 * @param <T>  Model类
 */
public interface IDao<M extends Mapper<T>, T> {

    T selectOne(T entity);

    T selectById(Object id);

    List<T> selectList(T entity);

    List<T> selectByExample(Example example);

    List<T> selectAll();

    int selectCount(T entity);

    int selectCountByExample(Example example);


    int insert(T entity);

    int insertSelective(T entity);


    int updateById(T entity);

    int updateByIdSelective(T entity);

    int updateByExampleSelective(T entity, Example example);


    int deleteById(Object id);

    int deleteByExample(Example example);


    void setMapper(M mapper);

    M getMapper();
}
