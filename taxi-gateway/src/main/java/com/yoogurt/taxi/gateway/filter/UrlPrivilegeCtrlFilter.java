package com.yoogurt.taxi.gateway.filter;

import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * Description:
 * URL访问权限控制Filter
 * @Author Eric Lau
 * @Date 2017/9/5.
 */
public class UrlPrivilegeCtrlFilter extends AccessControlFilter {
    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        HttpServletRequest req = WebUtils.toHttp(servletRequest);
        String authorization = req.getHeader("Authorization");
        return true;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest req = WebUtils.toHttp(servletRequest);
        String authorization = req.getHeader("Authorization");
        return true;
    }
}
