package com.yoogurt.taxi.gateway.shiro;

import com.yoogurt.taxi.dal.model.UserInfo;
import com.yoogurt.taxi.gateway.rest.IUserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Description:
 * 用于实现shiro的鉴权（Authentication）和授权（Authorization）。
 * @Author Eric Lau
 * @Date 2017/9/5.
 */
@Component("shiroRealm")
public class ShiroRealm extends AuthorizingRealm{

    @Autowired
    private IUserService userService;

    /**
     * <p>授权方法，标识用户能访问的url。</p>
     * Retrieves the AuthorizationInfo for the given principals from the underlying data store.  When returning
     * an instance from this method, you might want to consider using an instance of
     * {@link SimpleAuthorizationInfo SimpleAuthorizationInfo}, as it is suitable in most cases.
     *
     * @param principals the primary identifying principals of the AuthorizationInfo that should be retrieved.
     * @return the AuthorizationInfo associated with this principals.
     * @see SimpleAuthorizationInfo
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

        return authorizationInfo;
    }

    /**
     * <p>鉴权方法，登录必经之路。</p>
     * Retrieves authentication data from an implementation-specific datasource (RDBMS, LDAP, etc) for the given
     * authentication token.
     * <p/>
     * For most datasources, this means just 'pulling' authentication data for an associated subject/user and nothing
     * more and letting Shiro do the rest.  But in some systems, this method could actually perform EIS specific
     * log-in logic in addition to just retrieving data - it is up to the Realm implementation.
     * <p/>
     * A {@code null} return value means that no account could be associated with the specified token.
     *
     * @param authToken the authentication token containing the user's principal and credentials.
     * @return an {@link AuthenticationInfo} object containing account data resulting from the
     * authentication ONLY if the lookup is successful (i.e. account exists and is valid, etc.)
     * @throws AuthenticationException if there is an error acquiring data or performing
     *                                 realm-specific authentication logic for the specified <tt>token</tt>
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authToken) throws AuthenticationException {

        if (authToken instanceof UserAuthenticationToken) {
            UserAuthenticationToken token = (UserAuthenticationToken) authToken;
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//            String password = encoder.encode(new String(token.getPassword()));
            String username = token.getUsername();
            UserInfo userInfo = userService.getUserInfo(username, new String(token.getPassword()));
            if(userInfo == null) throw new AuthenticationException("登录失败，请核对登录名和密码");
            return new SimpleAuthenticationInfo(username, token.getPassword(), getName());
        }
        return null;
    }
}
