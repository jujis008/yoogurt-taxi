package com.yoogurt.taxi.gateway.filter;

import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.gateway.shiro.TokenHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class UserTokenFilter extends AccessControlFilter {

    @Autowired
    private TokenHelper tokenHelper;

    /**
     * 路径匹配工具
     */
    private PathMatcher matcher;

    /**
     * 符合此路径规则的uri将会被忽略
     */
    private static final String IGNORE_PATTERN = "/**/i/**";


    public PathMatcher getMatcher() {
        return matcher;
    }

    public void setMatcher(PathMatcher matcher) {
        this.matcher = matcher;
    }

    public UserTokenFilter() {
        this.matcher = new AntPathMatcher();
    }

    public UserTokenFilter(PathMatcher matcher) {
        if (matcher == null) {
            this.matcher = new AntPathMatcher();
        } else {
            this.matcher = matcher;
        }
    }

    /**
     * Returns <code>true</code> if the request is allowed to proceed through the filter normally, or <code>false</code>
     * if the request should be handled by the
     * {@link #onAccessDenied(ServletRequest, ServletResponse, Object) onAccessDenied(request,response,mappedValue)}
     * method instead.
     *
     * @param request     the incoming <code>ServletRequest</code>
     * @param response    the outgoing <code>ServletResponse</code>
     * @param mappedValue the filter-specific config value mapped to this filter in the URL rules mappings.
     * @return <code>true</code> if the request should proceed through the filter normally, <code>false</code> if the
     * request should be processed by this filter's
     * {@link #onAccessDenied(ServletRequest, ServletResponse, Object)} method instead.
     * @throws Exception if an error occurs during processing.
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        HttpServletRequest req = WebUtils.toHttp(request);
        String uri = WebUtils.getPathWithinApplication(req);
        //uri忽略处理
        if(matcher.match(IGNORE_PATTERN, uri)) return true;

        //token过期判定
        String token = tokenHelper.getAuthToken(req);
        return !tokenHelper.isTokenExpired(token);
    }

    /**
     * Processes requests where the subject was denied access as determined by the
     * {@link #isAccessAllowed(ServletRequest, ServletResponse, Object) isAccessAllowed}
     * method.
     *
     * @param request  the incoming <code>ServletRequest</code>
     * @param response the outgoing <code>ServletResponse</code>
     * @return <code>true</code> if the request should continue to be processed; false if the subclass will
     * handle/render the response directly.
     * @throws Exception if there is an error processing the request.
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        String currentUrl = this.getPathWithinApplication(request);
        log.warn("Login Expire: " + currentUrl);
        return onLoginExpire(request, response);
    }

    private boolean onLoginExpire(ServletRequest request, ServletResponse response) {
        try {
            ResponseObj result = ResponseObj.fail(StatusCode.LOGIN_EXPIRE.getStatus(), StatusCode.LOGIN_EXPIRE.getDetail());
            HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpServletResponse.setHeader("Content-type", "application/json;charset=UTF-8");
            httpServletResponse.getWriter().write(result.toJSON());
        } catch (IOException e) {
            log.error("onLoginExpire: {}", e);
        }
        return false;
    }
}
