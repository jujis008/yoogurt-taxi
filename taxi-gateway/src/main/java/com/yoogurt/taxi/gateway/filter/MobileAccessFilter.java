package com.yoogurt.taxi.gateway.filter;

import com.google.common.collect.Sets;
import com.yoogurt.taxi.common.enums.StatusCode;
import com.yoogurt.taxi.common.vo.ResponseObj;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
        if(canAccessPostRequest(request)) return true;
        if(!super.isAccessAllowed(request, response, mappedValue)) return false;
        // 获取当前用户
        Subject subject = this.getSubject(request, response);
        // 获取当前用户的URL
        String currentUrl = this.getPathWithinApplication(request);
        if (!subject.isPermitted(currentUrl)) {
            log.info("User: [" + subject.getPrincipal() + "] access denied on URL: " + currentUrl);
            return false;
        }
        return true;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        return sendChallenge(request, response);
    }

    protected boolean canAccessPostRequest(ServletRequest request) {
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        String url = WebUtils.getPathWithinApplication(httpRequest);
        return ignoreUris.contains(url);
    }


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
