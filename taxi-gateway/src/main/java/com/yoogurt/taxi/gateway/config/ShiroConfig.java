package com.yoogurt.taxi.gateway.config;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yoogurt.taxi.gateway.filter.AccessFilter;
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

    @Bean("securityManager")
    public SecurityManager getSecurityManager() {
        log.info("初始化SecurityManager，设置ShiroRealm。");
        return new DefaultWebSecurityManager(shiroRealm);
    }

    @Bean("shiroFilterFactoryBean")
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(SecurityManager securityManager) {

        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        bean.setSecurityManager(securityManager);
        bean.setLoginUrl("/login");
        Map<String, Filter> filterMap = Maps.newHashMap();
        filterMap.put("urlPrivilege", getUrlPrivilegeCtrlFilter());
        filterMap.put("accessFilter", getAccessFilter());
        bean.setFilters(filterMap);


        Map<String, String> chains = Maps.newLinkedHashMap();

        chains.put("/**/**.js", "anon");
        chains.put("/**.ico", "anon");
        chains.put("/**/**.css", "anon");
        chains.put("/**/**.html", "anon");
        chains.put("/resource/**", "anon");
        chains.put("/img/**", "anon");
        chains.put("/ticket/**", "anon");

        // 配置不会被拦截的链接 顺序判断
        chains.put("/static/**", "anon");
        chains.put("/**.html", "noSessionCreation");
        chains.put("/mobile/**", "noSessionCreation,accessFilter");

        // <!-- 过滤链定义，从上向下顺序执行，一般将 /**放在最为下边 -->:这是一个坑呢，一不小心代码就不好使了;
        // <!-- authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问-->
        chains.put("/**", "authc");

        bean.setFilterChainDefinitionMap(chains);

        log.info("ShiroFilterFactoryBean。");
        return bean;
    }


    @Bean(name = "accessFilter")
    public AccessFilter getAccessFilter() {
        Set<String> ignoreUrls = Sets.newHashSet(
                "/mobile/user/doLogin"
        );
        return new AccessFilter(ignoreUrls);
    }

    @Bean(name = "urlPrivilegeCtrlFilter")
    public UrlPrivilegeCtrlFilter getUrlPrivilegeCtrlFilter() {
        return new UrlPrivilegeCtrlFilter();
    }
}
