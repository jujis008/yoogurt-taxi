package com.yoogurt.taxi.common.factory;

import com.github.pagehelper.Page;
import com.yoogurt.taxi.common.pager.Pager;

/**
 * Description:
 * 分页对象的工厂类
 * @Author Eric Lau(weihao.liu@gogen.com.cn)
 * @Date 2017/8/2.
 */
public interface PagerFactory {

    /**
     * 生成一个分页对象。
     *
     * 调用此方法的套路如下：
     *
     * PageHelper.startPage(condition.getPageNum(), condition.getPageSize());
     * List<T> dataList = mapper.selectByExample(example);
     * PagerFactory.generatePager((Page<T>) applyList);
     *
     * 以上有一次强转动作，那是因为经过了PageHelper分页组件，Mapper返回的其实是Page对象。
     * 所以没有调用PageHelper.startPage方法，强转将会出错，请注意！
     *
     * @param page PageHelper提供的分页对象
     * @param <E> 结果集的数据类型
     * @return 系统内定义的分页对象
     */
    <E> Pager<E> generatePager(Page<E> page);
}
