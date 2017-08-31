package com.yoogurt.taxi.common.factory;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.yoogurt.taxi.common.pager.Pager;
import com.yoogurt.taxi.common.pager.WebPager;
import org.springframework.stereotype.Component;

/**
 * Description:
 * 生成Web端分页对象的工厂类
 * @author Eric Lau
 * @Date 2017/8/2.
 */
@Component("webPagerFactory")
public class WebPagerFactory implements PagerFactory {

    @Override
    public <E> Pager<E> generatePager(Page<E> page) {
        PageInfo<E> pageInfo = new PageInfo<>(page);
        return new WebPager<>(pageInfo);
    }
}
