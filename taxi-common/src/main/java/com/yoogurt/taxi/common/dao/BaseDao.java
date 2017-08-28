package com.yoogurt.taxi.common.dao;

import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Description:
 * <p>公共数据访问层，封装了一些基本的CRUD操作。其中：</p>
 * <p>M：Mapper类</p>
 * <p>T：实体类</p>
 * @Author Eric Lau
 * @Date 2017/8/28.
 */
public abstract class BaseDao<M extends Mapper<T>, T> implements IDao<M, T> {

    @Autowired
    private M mapper;


    @Override
    public T selectOne(T entity) {
        return mapper.selectOne(entity);
    }

    @Override
    public T selectById(Object id) {
        return mapper.selectByPrimaryKey(id);
    }

    @Override
    public List<T> selectList(T entity) { return mapper.select(entity); }

    @Override
    public  List<T> selectByExample(Example example){ return mapper.selectByExample(example); }

    @Override
    public List<T> selectAll() {
        return mapper.selectAll();
    }

    @Override
    public int selectCount(T entity) { return mapper.selectCount(entity); }

    @Override
    public int selectCountByExample(Example example){ return mapper.selectCountByExample(example); }



    @Override
    public int insert(T entity) { return mapper.insert(entity); }

    @Override
    public int insertSelective(T entity) { return mapper.insertSelective(entity); }



    @Override
    public int updateById(T entity) { return mapper.updateByPrimaryKey(entity); }

    @Override
    public int updateByIdSelective(T entity) { return mapper.updateByPrimaryKeySelective(entity); }

    @Override
    public int updateByExampleSelective(T entity, Example example){ return mapper.updateByExampleSelective(entity, example); }



    @Override
    public int deleteById(Object id) { return mapper.deleteByPrimaryKey(id); }

    @Override
    public int deleteByExample(Example example) { return mapper.deleteByExample(example); }



    @Override
    public void setMapper(M mapper) {
        this.mapper = mapper;
    }

    @Override
    public M getMapper() { return mapper; }
}
