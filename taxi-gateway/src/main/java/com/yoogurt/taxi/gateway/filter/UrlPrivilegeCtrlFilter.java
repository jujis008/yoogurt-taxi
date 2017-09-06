package com.yoogurt.taxi.gateway.filter;

import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.vo.ResponseObj;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Description:
 * URL访问权限控制Filter，所有的URI会经过此过滤器。
 * @Author Eric Lau
 * @Date 2017/9/5.
 */
@Slf4j
public class UrlPrivilegeCtrlFilter extends AccessControlFilter {

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {

        // 获取当前用户
        Subject subject = this.getSubject(request, response);
        // 获取当前用户的URL
        String currentUrl = this.getPathWithinApplication(request);
        if (subject != null && subject.isAuthenticated()) {
            if (!subject.isPermitted(currentUrl)) {
                log.info("User: [" + subject.getPrincipal() + "] access denied on URL: " + currentUrl);
                onDeny(request, response);
                return false;
            }
        } else {
            onDeny(request, response);
            return false;
        }
        return true;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        Subject subject = this.getSubject(request, response);
        String currentUrl = this.getPathWithinApplication(request);
        if (subject != null) {
            log.info("URL:" + currentUrl + " [user:" + subject.getPrincipal() + "]");
        }
        onDeny(request, response);
        return false;
    }

    /**
     * 如果没有权限，抛出无权限访问异常，前端处理
     * @param request request
     * @param response response
     * @throws IOException
     */
    private void onDeny(ServletRequest request, ServletResponse response) throws IOException {
        String currentUrl = this.getPathWithinApplication(request);
        ResponseObj result = ResponseObj.fail(StatusCode.NO_AUTHORITY.getStatus(),
                "抱歉，您没有访问URL:" + currentUrl + "的权限，请联系系统管理员。");

        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value()); //设置状态码
        httpServletResponse.setHeader("Content-type", "application/json;charset=UTF-8");
        httpServletResponse.setHeader("Cache-Control", "no-cache, must-revalidate");
        httpServletResponse.getWriter().write(result.toJSON());
    }
}
