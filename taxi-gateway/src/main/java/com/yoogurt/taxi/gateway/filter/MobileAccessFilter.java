package com.yoogurt.taxi.gateway.filter;

import com.auth0.jwt.JWTVerifier;
import com.google.common.collect.Sets;
import com.yoogurt.taxi.common.bo.SessionUser;
import com.yoogurt.taxi.common.constant.CacheKey;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.helper.RedisHelper;
import com.yoogurt.taxi.common.vo.ResponseObj;
import com.yoogurt.taxi.gateway.service.AuthService;
import com.yoogurt.taxi.gateway.shiro.TokenHelper;
import com.yoogurt.taxi.gateway.shiro.UserAuthenticationToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Description:
 * shiro过滤器，针对以 /mobile 开头的URI进行拦截处理。
 * 优先于 {@link UrlPrivilegeCtrlFilter} UrlPrivilegeCtrlFilter。
 * 在{@link BasicHttpAuthenticationFilter} BasicHttpAuthenticationFilter的基础上，
 * 增加了对ignoreUris的处理
 * @Author Eric Lau
 * @Date 2017/9/5
 */
@Slf4j
public class MobileAccessFilter extends BasicHttpAuthenticationFilter {

    @Autowired
    private AuthService authService;

    @Autowired
    private RedisHelper redisHelper;

    @Autowired
    private TokenHelper tokenHelper;

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
    }

    public MobileAccessFilter(Set<String> ignoreUris) {
        this();
        this.ignoreUris = ignoreUris;
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
        //在Basic基础上，增加对ignoreUris的处理能力
        return canAccessPostRequest(request) || super.isAccessAllowed(request, response, mappedValue);
    }

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {

        log.info("mobile####createToken#####" + SecurityUtils.getSubject().isAuthenticated());
        //createToken方法调用的前置条件是 isLoginAttempt(request, response) == true
        //这意味着userId和SessionUser一定有值，无需判空
        String userId = tokenHelper.getUserId(WebUtils.toHttp(request)).toString();
        SessionUser user = (SessionUser) redisHelper.getObject(CacheKey.SESSION_USER_KEY + userId);

        //伪造一份授权码，内部二次登录，可以保证安全。
        redisHelper.set(CacheKey.GRANT_CODE_KEY + user.getUserId(), user.getGrantCode(), 300);

        String username = user.getUsername();
        String credentials = DigestUtils.md5DigestAsHex((userId + username).getBytes());
        //构造shiro使用的Token对象
        UserAuthenticationToken token = new UserAuthenticationToken(username, credentials.toCharArray());
        token.setUserId(userId);
        token.setGrantCode(user.getGrantCode());
        token.setRememberMe(true);
        token.setLoginAgain(true);
        return token;

    }

    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        Object userId = tokenHelper.getUserId(WebUtils.toHttp(request));
        return super.isLoginAttempt(request, response) && userId != null && redisHelper.getObject(CacheKey.SESSION_USER_KEY + userId) != null;
    }

    /**
     * 判断本次请求是否可以忽略
     * @param request 请求request
     * @return 是否可以忽略
     */
    private boolean canAccessPostRequest(ServletRequest request) {
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        String url = WebUtils.getPathWithinApplication(httpRequest);
        return ignoreUris.contains(url);
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
            ResponseObj result = ResponseObj.fail(StatusCode.NO_AUTHORITY.getStatus(),
                    "抱歉，您没有访问URL:" + currentUrl + "的权限，请联系系统管理员。");
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
