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
public abstract class BaseDao<M extends Mapper<T>, T> {

    @Autowired
    private M mapper;



    public T selectOne(T entity) {
        return mapper.selectOne(entity);
    }

    public T selectById(Object id) {
        return mapper.selectByPrimaryKey(id);
    }

    public List<T> selectList(T entity) { return mapper.select(entity); }

    public  List<T> selectByExample(Example example){ return mapper.selectByExample(example); }

    public List<T> selectAll() {
        return mapper.selectAll();
    }

    public int selectCount(T entity) { return mapper.selectCount(entity); }

    public int selectCountByExample(Example example){ return mapper.selectCountByExample(example); }



    public int insert(T entity) { return mapper.insert(entity); }

    public int insertSelective(T entity) { return mapper.insertSelective(entity); }



    public int updateById(T entity) { return mapper.updateByPrimaryKey(entity); }

    public int updateByIdSelective(T entity) { return mapper.updateByPrimaryKeySelective(entity); }

    public int updateByExampleSelective(T entity, Example example){ return mapper.updateByExampleSelective(entity, example); }



    public int deleteById(Object id) { return mapper.deleteByPrimaryKey(id); }

    public int deleteByExample(Example example) { return mapper.deleteByExample(example); }



    public void setMapper(M mapper) {
        this.mapper = mapper;
    }

    public M getMapper() {
        return mapper;
    }
}
