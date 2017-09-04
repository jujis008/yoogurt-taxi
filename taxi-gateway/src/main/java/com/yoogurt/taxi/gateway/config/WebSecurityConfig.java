package com.yoogurt.taxi.gateway.config;

import com.yoogurt.taxi.gateway.jwt.JwtAuthenticationTokenFilter;
import com.yoogurt.taxi.gateway.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * ${DESCRIPTION}
 *
 * @author wanghaobin
 * @create 2017-06-02 12:02
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthService authService;
    @Value("${gate.ignore.startWith}")
    private String startWith;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin().loginPage("/login").defaultSuccessUrl("/auth").permitAll().and()
                .logout().logoutSuccessUrl("/login").invalidateHttpSession(true).and().authorizeRequests()
                .antMatchers("/**/*.css", "/img/**", "/**/*.js", "/mobile/**", "/*/mobile/**") // 放开"/*/mobile/**"，通过oauth2.0来鉴权
                .permitAll().and().authorizeRequests().antMatchers("/**").authenticated();
        http.csrf().disable();
        http.headers().frameOptions().disable();
        http.httpBasic();
        // 添加JWT filter
        http.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);

        // 禁用缓存
        http.headers().cacheControl();
        http.headers().contentTypeOptions().disable();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(authService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Bean
    public JwtAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
        return new JwtAuthenticationTokenFilter();
    }

}
