package com.yoogurt.taxi.gateway.config;

import com.google.common.collect.Maps;
import com.yoogurt.taxi.gateway.filter.MobileAccessFilter;
import com.yoogurt.taxi.gateway.filter.UniqueDeviceFilter;
import com.yoogurt.taxi.gateway.filter.UrlPrivilegeCtrlFilter;
import com.yoogurt.taxi.gateway.filter.UserTokenFilter;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.Map;

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
        filterMap.put("userTokenFilter", getUserTokenFilter());
        filterMap.put("uniqueDeviceFilter", getUniqueDeviceFilter());
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
        //需要拦截的移动端uri
        //uniqueDeviceFilter在userTokenFilter之后
        chains.put("/mobile/**", "noSessionCreation,userTokenFilter,uniqueDeviceFilter,mobileAccessFilter");
        //需要拦截的后台uri
        chains.put("/web/**", "userTokenFilter,urlPrivilegeFilter");

        // <!-- 过滤链定义，从上向下顺序执行，一般将 /** 放在最下边 -->
        chains.put("/**", "user");

        bean.setFilterChainDefinitionMap(chains);

        log.info("初始化ShiroFilterFactoryBean");
        return bean;
    }

    @Bean("securityManager")
    public SecurityManager getSecurityManager() {
        log.info("初始化SecurityManager，设置ShiroRealm。");
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager(shiroRealm);
        //cookie功能加持
        securityManager.setRememberMeManager(getRememberMeManager());
        return securityManager;
    }

    /**
     * 移动端过滤器，含shiro重新登录的业务逻辑
     * @return MobileAccessFilter
     */
    @Bean(name = "mobileAccessFilter")
    public MobileAccessFilter getAccessFilter() {
        return new MobileAccessFilter();
    }

    /**
     * Token过期验证过滤器
     * @return UserTokenFilter
     */
    @Bean(name = "userTokenFilter")
    public UserTokenFilter getUserTokenFilter() {
        return new UserTokenFilter();
    }

    /**
     * 同账号，多设备互踢过滤器
     * @return UniqueDeviceFilter
     */
    @Bean(name = "uniqueDeviceFilter")
    public UniqueDeviceFilter getUniqueDeviceFilter() {
        return new UniqueDeviceFilter();
    }

    /**
     * web端过滤器，主要对登录状态的判断
     * @return UrlPrivilegeCtrlFilter
     */
    @Bean(name = "urlPrivilegeCtrlFilter")
    public UrlPrivilegeCtrlFilter getUrlPrivilegeCtrlFilter() {
        UrlPrivilegeCtrlFilter ctrlFilter = new UrlPrivilegeCtrlFilter();
        ctrlFilter.setLoginUrl("/login");
        return ctrlFilter;
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
        // 7天后过期
        cookie.setMaxAge(604800);
        cookie.setHttpOnly(true);
        cookie.setName("yoogurt-taxi");
        rememberMeManager.setCookie(cookie);
        return rememberMeManager;
    }

    @Bean(name = "redisCacheManager")
    @Deprecated
    public RedisCacheManager getRedisCacheManager() {
        return new RedisCacheManager();
    }
}
