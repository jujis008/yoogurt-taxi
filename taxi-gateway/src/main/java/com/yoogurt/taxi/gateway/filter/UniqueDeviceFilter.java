package com.yoogurt.taxi.gateway.filter;

import com.yoogurt.taxi.common.bo.SessionUser;
import com.yoogurt.taxi.common.constant.CacheKey;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.helper.RedisHelper;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.gateway.shiro.TokenHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
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

/**
 * 一个账号只能在一台设备上登录，后一次登录会将前一次登录的设备踢出。
 * 此过滤器需要配置在{@link UserTokenFilter} UserTokenFilter之后。
 */
@Slf4j
public class UniqueDeviceFilter extends AccessControlFilter {

    @Autowired
    private TokenHelper tokenHelper;

    @Autowired
    private RedisHelper redisHelper;

    private PathMatcher matcher;

    /**
     * 符合此路径规则的uri将会被忽略
     */
    private static final String IGNORE_PATTERN = "/**/i/**";


    public UniqueDeviceFilter() {
        this.matcher = new AntPathMatcher();
    }

    public UniqueDeviceFilter(PathMatcher matcher) {
        if (matcher == null) {
            this.matcher = new AntPathMatcher();
        } else {
            this.matcher = matcher;
        }
    }

    public PathMatcher getMatcher() {
        return matcher;
    }

    public void setMatcher(PathMatcher matcher) {
        this.matcher = matcher;
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
        //被忽略的uri不作处理
        if(matcher.match(IGNORE_PATTERN, uri)) return true;
        //验证token是否过期
        String authToken = tokenHelper.getAuthToken(req);
        String userId = tokenHelper.getUserId(authToken);
        Object obj = redisHelper.getObject(CacheKey.SESSION_USER_KEY + userId);
        if (obj == null) return false;
        SessionUser user = (SessionUser) obj;
        //判断redis缓存的token与客户端传入的token是一致的
        //token不一致说明存在多设备登录，不允许通过
        return authToken.equals(user.getToken());
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
        log.warn("kick out user: " + SecurityUtils.getSubject().getPrincipal());
        return kickOut(request, response);
    }

    public boolean kickOut(ServletRequest request, ServletResponse response) {
        try {
            ResponseObj result = ResponseObj.fail(StatusCode.KICK_OUT.getStatus(), StatusCode.KICK_OUT.getDetail());
            HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpServletResponse.setHeader("Content-type", "application/json;charset=UTF-8");
            httpServletResponse.getWriter().write(result.toJSON());
        } catch (IOException e) {
            log.error("kickOut: {}", e);
        }
        return false;
    }
}
