package com.yoogurt.taxi.gateway.config;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yoogurt.taxi.gateway.filter.MobileAccessFilter;
import com.yoogurt.taxi.gateway.filter.UrlPrivilegeCtrlFilter;
import com.yoogurt.taxi.gateway.shiro.ShiroRealm;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.Map;
import java.util.Set;

/**
 * Description:
 *
 * @Author Eric Lau
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
        chains.put("/info", "anon");
        chains.put("/env", "anon");
        chains.put("/shutdown", "anon");
        chains.put("/health", "anon");
        chains.put("/metrics", "anon");
        chains.put("/dump", "anon");
        chains.put("/configprops", "anon");
        chains.put("/autoconfig", "anon");
        chains.put("/loggers", "anon");
        chains.put("/mappings", "anon");
        chains.put("/trace", "anon");
        chains.put("/actuator", "anon");
        chains.put("/autoconfig", "anon");
        chains.put("/readness", "anon");

        // noSessionCreation: 要求shiro不创建session
        chains.put("/**.html", "noSessionCreation");
        //移动端接口，对于不需要拦截的url，在mobileAccessFilter中的ignoreUrls配置
        chains.put("/mobile/**", "noSessionCreation,authcBasic[GET,POST,PUT,PATCH,DELETE],mobileAccessFilter");
        //后台接口
        chains.put("/web/**", "urlPrivilegeFilter");

        // <!-- 过滤链定义，从上向下顺序执行，一般将 /** 放在最下边 -->
        chains.put("/**", "authc");

        bean.setFilterChainDefinitionMap(chains);

        log.info("初始化ShiroFilterFactoryBean");
        return bean;
    }

    @Bean("securityManager")
    public SecurityManager getSecurityManager() {
        log.info("初始化SecurityManager，设置ShiroRealm。");
        return new DefaultWebSecurityManager(shiroRealm);
    }


    @Bean(name = "mobileAccessFilter")
    public MobileAccessFilter getAccessFilter() {
        Set<String> ignoreUris = Sets.newHashSet(
                "/mobile/user/login/",
                "/mobile/user/info/{id}"
        );
        return new MobileAccessFilter(ignoreUris);
    }

    @Bean(name = "urlPrivilegeCtrlFilter")
    public UrlPrivilegeCtrlFilter getUrlPrivilegeCtrlFilter() {
        return new UrlPrivilegeCtrlFilter();
    }
}
