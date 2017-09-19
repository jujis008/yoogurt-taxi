package com.yoogurt.taxi.gateway.filter;

import com.google.common.collect.Sets;
import com.yoogurt.taxi.common.bo.SessionUser;
import com.yoogurt.taxi.common.constant.CacheKey;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.helper.RedisHelper;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.common.helper.TokenHelper;
import com.yoogurt.taxi.gateway.shiro.UserAuthenticationToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.DigestUtils;
import org.springframework.util.PathMatcher;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/**
 * Description:
 * shiro过滤器，针对以 /mobile 开头的URI进行拦截处理。
 * 该过滤器的顺序是最后，前面的过滤器保证了token是存在的。
 * 在{@link BasicHttpAuthenticationFilter} BasicHttpAuthenticationFilter的基础上，
 * 增加了对ignoreUris的处理
 * @Author Eric Lau
 * @Date 2017/9/5
 */
@Slf4j
public class MobileAccessFilter extends BasicHttpAuthenticationFilter {

    @Autowired
    private RedisHelper redisHelper;

    @Autowired
    private TokenHelper tokenHelper;

    private PathMatcher matcher;

    /**
     * 符合此路径规则的uri将会被忽略
     */
    private static final String IGNORE_PATTERN = "/**/i/**";

    /**
     * 应用名将出现在查询令牌时，默认是<code>application</code>。 可以通过
     * {@link #setApplicationName(String) setApplicationName} 方法覆盖
     */
    private static final String APPLICATION_NAME = "yoogurt-taxi";

    /**
     * 可以忽略的URI
     */
    private Set<String> ignoreUris = Sets.newHashSet();

    public MobileAccessFilter() {
        super.setApplicationName(APPLICATION_NAME);
        this.matcher = new AntPathMatcher();
    }

    public MobileAccessFilter(Set<String> ignoreUris) {
        this();
        this.ignoreUris = ignoreUris;
    }

    public PathMatcher getMatcher() {
        return matcher;
    }

    public void setMatcher(PathMatcher matcher) {
        this.matcher = matcher;
    }

    /**
     * The Basic authentication filter can be configured with a list of HTTP methods to which it should apply. This
     * method ensures that authentication is <em>only</em> required for those HTTP methods specified. For example,
     * if you had the configuration:
     * <pre>
     *    [urls]
     *    /basic/** = authcBasic[POST,PUT,DELETE]
     * </pre>
     * then a GET request would not required authentication but a POST would.
     *
     * @param request     The current HTTP servlet request.
     * @param response    The current HTTP servlet response.
     * @param mappedValue The array of configured HTTP methods as strings. This is empty if no methods are configured.
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
//        log.info("########## " + SecurityUtils.getSubject().isAuthenticated() + " #########");
        HttpServletRequest req = WebUtils.toHttp(request);
        String uri = WebUtils.getPathWithinApplication(req);
        //在Basic基础上，增加对ignoreUris的处理能力
        return matcher.match(IGNORE_PATTERN, uri) || canAccessPostRequest(req) || super.isAccessAllowed(request, response, mappedValue);
    }

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {

        //createToken方法调用的前置条件是 isLoginAttempt(request, response) == true
        //这意味着userId和SessionUser一定有值，无需判空
        String authToken = tokenHelper.getAuthToken(WebUtils.toHttp(request));
        Long userId = tokenHelper.getUserId(authToken);
        SessionUser user = (SessionUser) redisHelper.getObject(CacheKey.SESSION_USER_KEY + userId);

        //伪造一份授权码，内部二次登录，可以保证安全。
        redisHelper.set(CacheKey.GRANT_CODE_KEY + user.getUserId(), user.getGrantCode(), 300);

        String username = user.getUsername();
        String credentials = DigestUtils.md5DigestAsHex((userId + username).getBytes());
        //构造shiro使用的Token对象
        UserAuthenticationToken token = new UserAuthenticationToken(username, credentials.toCharArray());
        token.setUserId(userId);
        token.setToken(authToken);
        token.setGrantCode(user.getGrantCode());

        token.setRememberMe(true);
        token.setLoginAgain(true);
        return token;
    }

    /**
     * 判断本次请求是否可以忽略
     * @param request request请求
     * @return 是否可以忽略
     */
    private boolean canAccessPostRequest(HttpServletRequest request) {
        String uri = WebUtils.getPathWithinApplication(request);
        return ignoreUris.contains(uri);
    }


    /**
     * 添加可以忽略的uri
     * @param uri 要添加的uri
     */
    public void addIgnoreUri(String uri) {
        ignoreUris.add(uri);
    }

    /**
     * Builds the challenge for authorization by setting a HTTP <code>401</code> (Unauthorized) status as well as the
     * response's {@link #AUTHENTICATE_HEADER AUTHENTICATE_HEADER}.
     * <p/>
     * The header value constructed is equal to:
     * <p/>
     * <code>{@link #getAuthcScheme() getAuthcScheme()} + " realm=\"" + {@link #getApplicationName() getApplicationName()} + "\"";</code>
     *
     * @param request  incoming ServletRequest, ignored by this implementation
     * @param response outgoing ServletResponse
     * @return false - this sends the challenge to be sent back
     */
    @Override
    protected boolean sendChallenge(ServletRequest request, ServletResponse response) {
        try {
            super.sendChallenge(request, response);
            String currentUrl = this.getPathWithinApplication(request);
            ResponseObj result = ResponseObj.fail(StatusCode.LOGIN_EXPIRE, StatusCode.LOGIN_EXPIRE.getDetail());
            HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpServletResponse.setHeader("Content-type", "application/json;charset=UTF-8");
            httpServletResponse.getWriter().write(result.toJSON());
            log.warn("Access denied on URL: " + currentUrl);
        } catch (IOException e) {
            log.error("sendChallenge: {}", e);
        }
        return false;
    }
}
