package com.yoogurt.taxi.gateway.shiro;

import lombok.Getter;
import lombok.Setter;
import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * Description:
 * 基于shiro的用户鉴权凭证
 * @Author Eric Lau
 * @Date 2017/9/5.
 */
@Setter
@Getter
public class UserAuthenticationToken extends UsernamePasswordToken {

    private String from = RequestFrom.TAXI_MOBILE.toString();

    public UserAuthenticationToken() {
    }

    /**
     * Constructs a new UsernamePasswordToken encapsulating the username and password submitted
     * during an authentication attempt, with a <tt>null</tt> {@link #getHost() host} and a
     * <tt>rememberMe</tt> default of <tt>false</tt>.
     *
     * @param username the username submitted for authentication
     * @param password the password character array submitted for authentication
     */
    public UserAuthenticationToken(String username, char[] password) {
        super(username, password);
    }

    /**
     * Constructs a new UsernamePasswordToken encapsulating the username and password submitted
     * during an authentication attempt, with a <tt>null</tt> {@link #getHost() host} and
     * a <tt>rememberMe</tt> default of <tt>false</tt>
     * <p/>
     * <p>This is a convenience constructor and maintains the password internally via a character
     * array, i.e. <tt>password.toCharArray();</tt>.  Note that storing a password as a String
     * in your code could have possible security implications as noted in the class JavaDoc.</p>
     *
     * @param username the username submitted for authentication
     * @param password the password string submitted for authentication
     */
    public UserAuthenticationToken(String username, String password, String from) {
        super(username, password);
        this.from = from;
    }

    public enum RequestFrom {
        TAXI_MOBILE, //来自移动端的请求
        TAXI_WEB,;  //来自后台管理系统的请求
    }
}
