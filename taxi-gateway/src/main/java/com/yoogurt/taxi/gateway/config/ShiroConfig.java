package com.yoogurt.taxi.gateway.config;

import com.google.common.collect.Maps;
import com.yoogurt.taxi.gateway.shiro.ShiroRealm;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

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
        return new DefaultSecurityManager(shiroRealm);
    }

    @Bean("shiroFilterFactoryBean")
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(SecurityManager securityManager) {

        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        bean.setSecurityManager(securityManager);
        bean.setLoginUrl("/login");

        Map<String, String> filerMap = Maps.newLinkedHashMap();

        // 配置不会被拦截的链接 顺序判断
        filerMap.put("/static/**", "anon");
        filerMap.put("/error", "anon");
        // 配置退出过滤器,其中的具体的退出代码Shiro已经替我们实现了
        filerMap.put("/logout", "logout");

        // <!-- 过滤链定义，从上向下顺序执行，一般将 /**放在最为下边 -->:这是一个坑呢，一不小心代码就不好使了;
        // <!-- authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问-->
        filerMap.put("/**", "authc");

        bean.setFilterChainDefinitionMap(filerMap);

        log.info("ShiroFilterFactoryBean。");
        return bean;
    }

}
