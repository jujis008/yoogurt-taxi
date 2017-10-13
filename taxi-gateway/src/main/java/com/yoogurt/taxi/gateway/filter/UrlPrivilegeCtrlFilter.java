package com.yoogurt.taxi.gateway.filter;

import com.yoogurt.taxi.common.bo.SessionUser;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.helper.TokenHelper;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.dal.beans.UserInfo;
import com.yoogurt.taxi.dal.enums.UserType;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

/**
 * Description:
 * 针对/web/**形式的过滤器，主要是对uri的鉴权。
 * @author Eric Lau
 * @Date 2017/9/5.
 */
@Slf4j
public class UrlPrivilegeCtrlFilter extends AccessControlFilter {

    /**
     * 符合此路径规则的uri将会被忽略
     */
    private static final String IGNORE_PATTERN = "/**/i/**";

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {

        // 获取当前用户
        Subject subject = getSubject(request, response);
        // 获取当前用户的URL
        String currentUrl = getPathWithinApplication(request);
        AntPathMatcher matcher = new AntPathMatcher();
        if (matcher.match(IGNORE_PATTERN,currentUrl)) {
            return true;
        }
        Collection<SessionUser> userInfoList = subject.getPrincipals().fromRealm("UserInfo");
        for (SessionUser userInfo1:userInfoList) {
            if (UserType.getEnumsByCode(userInfo1.getType()) == UserType.SUPER_ADMIN) {
                return true;
            }
        }
        //判断当前用户是有该uri的访问权限
        if (!subject.isPermitted(currentUrl)) {
            log.info("User: [" + subject.getPrincipal() + "] access denied on URL: " + currentUrl);
            return false;
        }
        return true;
    }

    /**
     * isAccessAllowed(request, response)==false触发此方法。
     * @param request request
     * @param response response
     * @return always false
     * @throws Exception Exception
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        Subject subject = getSubject(request, response);
        String currentUrl = getPathWithinApplication(request);
        log.info("URL:" + currentUrl + " [user:" + subject.getPrincipal() + "]");
        onDeny(request, response);
        return false;
    }

    /**
     * 如果没有权限，抛出无权限访问异常，前端处理
     * @param request request
     * @param response response
     * @throws IOException IOException
     */
    private void onDeny(ServletRequest request, ServletResponse response) throws IOException {
        String currentUrl = this.getPathWithinApplication(request);
        ResponseObj result = ResponseObj.fail(StatusCode.NO_AUTHORITY.getStatus(),
                "抱歉，您没有访问URL:" + currentUrl + "的权限，请联系系统管理员。");
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        /** 设置状态码，此处若用401将会使得前端弹出一个输入用户名密码的Promotion，所以建议不要返回401状态码。 */
        httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
        httpServletResponse.setHeader("Content-type", "application/json;charset=UTF-8");
        httpServletResponse.setHeader("Cache-Control", "no-cache, must-revalidate");
        httpServletResponse.getWriter().write(result.toJSON());
    }
}
