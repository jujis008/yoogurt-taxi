package com.yoogurt.taxi.gateway.config;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yoogurt.taxi.gateway.filter.MobileAccessFilter;
import com.yoogurt.taxi.gateway.filter.UrlPrivilegeCtrlFilter;
import com.yoogurt.taxi.gateway.shiro.ShiroRealm;
import com.yoogurt.taxi.gateway.shiro.cache.RedisCacheManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;
import java.util.Map;
import java.util.Set;

/**
 * Description:
 * shiro配置中心
 * @author Eric Lau
 * @Date 2017/9/5.
 */
@Slf4j
@Configuration
public class ShiroConfig {

    @Autowired
    private ShiroRealm shiroRealm;

    /**
     * shiro拦截器总配置。
     * 内置的Filter如下所示：
     -----------------------------------------------------------------
     ||                anon AnonymousFilter                         ||
     ||                auth FormAuthenticationFilter                ||
     ||                authcBasic BasicHttpAuthenticationFilter     ||
     ||                logout LogoutFilter                          ||
     ||                noSessionCreation NoSessionCreationFilter    ||
     ||                perms PermissionsAuthorizationFilter         ||
     ||                port PortFilter                              ||
     ||                rest HttpMethodPermissionFilter              ||
     ||                roles RolesAuthorizationFilter               ||
     ||                ssl SslFilter                                ||
     ||                user UserFilter                              ||
     ================================================================
     * @param securityManager securityManager
     * @return shiroFilterFactoryBean
     */
    @Bean("shiroFilterFactoryBean")
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(SecurityManager securityManager) {

        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        bean.setSecurityManager(securityManager);
        bean.setLoginUrl("/login");
        Map<String, Filter> filterMap = Maps.newHashMap();
        //自定义Filter
        filterMap.put("urlPrivilegeFilter", getUrlPrivilegeCtrlFilter());
        filterMap.put("mobileAccessFilter", getAccessFilter());
        bean.setFilters(filterMap);


        Map<String, String> chains = Maps.newLinkedHashMap();

        // anon: 允许匿名访问
        chains.put("/**/**.js", "anon");
        chains.put("/**/**.ico", "anon");
        chains.put("/**/**.woff", "anon");
        chains.put("/**/**.css", "anon");
        chains.put("/**/**.html", "anon");
        chains.put("/resource/**", "anon");
        chains.put("/img/**", "anon");
        chains.put("/static/**", "anon");
        chains.put("/favicon.ico", "anon");
        chains.put("/login", "anon");
        chains.put("/login.html", "anon");
        chains.put("/refresh", "anon");
        chains.put("/info", "anon");
        chains.put("/env", "anon");
        chains.put("/beans", "anon");
        chains.put("/restart", "anon");
        chains.put("/health", "anon");
        chains.put("/metrics", "anon");
        chains.put("/dump", "anon");
        chains.put("/configprops", "anon");
        chains.put("/loggers", "anon");
        chains.put("/mappings", "anon");
        chains.put("/trace", "anon");
        chains.put("/actuator", "anon");
        chains.put("/autoconfig", "anon");
        chains.put("/readness", "anon");
        chains.put("/hystrix", "anon");
        //token颁发接口
        chains.put("/token/**", "anon");

        // noSessionCreation: 要求shiro不创建session
        chains.put("/**.html", "noSessionCreation");
        //移动端接口，对于不需要拦截的url，在mobileAccessFilter中的ignoreUrls配置
        chains.put("/mobile/**", "noSessionCreation,mobileAccessFilter");
        //后台接口
        chains.put("/web/**", "urlPrivilegeFilter");

        // <!-- 过滤链定义，从上向下顺序执行，一般将 /** 放在最下边 -->
        chains.put("/**", "user");

        bean.setFilterChainDefinitionMap(chains);

        log.info("初始化ShiroFilterFactoryBean");
        return bean;
    }

    @Bean("securityManager")
    public SecurityManager getSecurityManager() {
        log.info("初始化SecurityManager，设置ShiroRealm。");
        /** 开启了rememberMe功能，不要使用缓存，不好驾驭 ￣□￣｜｜*/
//        shiroRealm.setCacheManager(getRedisCacheManager());
//        shiroRealm.setCachingEnabled(true);
//        shiroRealm.setAuthenticationCachingEnabled(true);
//        shiroRealm.setAuthorizationCachingEnabled(true);

        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager(shiroRealm);
        securityManager.setRememberMeManager(getRememberMeManager());
//        securityManager.setCacheManager(getRedisCacheManager());
        /** 不要维护session */
//        securityManager.setSessionManager(new DefaultWebSessionManager());
        return securityManager;
    }

    @Bean(name = "redisCacheManager")
    public RedisCacheManager getRedisCacheManager() {
        return new RedisCacheManager();
    }

    @Bean(name = "mobileAccessFilter")
    public MobileAccessFilter getAccessFilter() {
        Set<String> ignoreUris = Sets.newHashSet(
                "/mobile/user/login"
        );
        return new MobileAccessFilter(ignoreUris);
    }

    /**
     * 因为需要开启rememberMe功能，需要注入一个RememberMeManager，
     * 通常情况，使用基于Cookie的实现方式。
     * @return RememberMeManager
     */
    @Bean
    public RememberMeManager getRememberMeManager() {
        CookieRememberMeManager rememberMeManager = new CookieRememberMeManager();
        rememberMeManager.setCipherKey(Base64.decode("4AvVhmFLUs0KTA3Kprsdag=="));
        SimpleCookie cookie = new SimpleCookie();
        cookie.setMaxAge(2592000);
        cookie.setHttpOnly(true);
        cookie.setName("yoogurt-taxi");
        rememberMeManager.setCookie(cookie);
        return rememberMeManager;
    }

    @Bean(name = "urlPrivilegeCtrlFilter")
    public UrlPrivilegeCtrlFilter getUrlPrivilegeCtrlFilter() {
        UrlPrivilegeCtrlFilter ctrlFilter = new UrlPrivilegeCtrlFilter();
        ctrlFilter.setLoginUrl("/login");
        return ctrlFilter;
    }
}
