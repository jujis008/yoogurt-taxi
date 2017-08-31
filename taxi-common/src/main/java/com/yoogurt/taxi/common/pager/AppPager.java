package com.yoogurt.taxi.common.pager;

import com.github.pagehelper.PageInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * Description:
 * App端分页对象
 * @author Eric Lau
 * @Date 2017/8/2.
 */
@Setter
@Getter
public class AppPager<E> extends Pager<E>{


    public AppPager() {
    }

    public AppPager(int pageNum, int pageSize) {
        super(pageNum, pageSize);
    }

    /**
     * 从 PageInfo 转换成 WebPager
     * @param page PageHelper返回的分页对象
     */
    public AppPager(PageInfo<E> page) {
        this();
        if(page != null){
            super.setPageNum(page.getPageNum());
            super.setPageSize(page.getPageSize());
            super.setDataList(page.getList());
        }
    }
}
