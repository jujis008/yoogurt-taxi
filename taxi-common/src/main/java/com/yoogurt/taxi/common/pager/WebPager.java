package com.yoogurt.taxi.common.pager;

import com.github.pagehelper.PageInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * Description:
 * web端分页对象
 * @author Eric Lau
 * @Date 2017/8/2.
 */
@Setter
@Getter
public class WebPager<E> extends BasePager<E> {

    /**
     * 是否有上一页
     */
    private boolean hasPreviousPage;

    /**
     * 是否有下一页
     */
    private boolean hasNextPage;

    /**
     * 总页数
     */
    private int pages;

    /**
     * 总记录数
     */
    private long total;

    public WebPager() {
    }

    public WebPager(int pageNum, int pageSize) {
        super(pageNum, pageSize);
    }

    /**
     * 从 PageInfo 转换成 WebPager
     * @param page PageHelper返回的分页对象
     */
    public WebPager(PageInfo<E> page) {
        this();
        if(page != null){
            super.setPageNum(page.getPageNum());
            super.setPageSize(page.getPageSize());
            this.pages = page.getPages();
            this.total = page.getTotal();
            this.hasNextPage = page.isHasNextPage();
            this.hasPreviousPage = page.isHasPreviousPage();
            super.setDataList(page.getList());
        }
    }
}

