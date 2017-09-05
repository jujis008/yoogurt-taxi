package com.yoogurt.taxi.gateway.filter;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * Description:
 * shiro过滤器
 * @Author Eric Lau
 * @Date 2017/9/5.
 */
@Slf4j
public class AccessFilter extends AuthenticatingFilter {

    /**
     * HTTP Authorization 头部, 值为<code>Authorization</code>
     */
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private Set<String> ignoreUrls = Sets.newHashSet();

    public AccessFilter() {
    }

    public AccessFilter(Set<String> ignoreUrls) {
        this.ignoreUrls = ignoreUrls;
    }

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest req = WebUtils.toHttp(request);
        String authorizationHeader = req.getHeader("Authorization");
        /*if (authorizationHeader == null || authorizationHeader.length() == 0) {
            // Create an empty authentication token since there is no
            // Authorization header.
            return new JsonWebToken("");
        }
        if (log.isDebugEnabled()) {
            log.debug("Attempting to execute login with headers [" + authorizationHeader + "]");
        }
        String[] authTokens = authorizationHeader.split(" ");
        if (authTokens == null || authTokens.length < 2) {
            return new JsonWebToken("");
        }
        return new JsonWebToken(authTokens[1]);*/
        return null;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest req = WebUtils.toHttp(request);
        String authorization = req.getHeader("Authorization");
        if (canAccessPostRequest(request, response)) {
            return true;
        }
        return true;
    }

    protected boolean canAccessPostRequest(ServletRequest request, ServletResponse response) {
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        String url = WebUtils.getPathWithinApplication(httpRequest);
        return ignoreUrls.contains(url);
    }
}
