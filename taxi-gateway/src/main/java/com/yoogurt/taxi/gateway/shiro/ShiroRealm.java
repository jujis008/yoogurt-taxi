package com.yoogurt.taxi.gateway.shiro;

import com.yoogurt.taxi.common.bo.SessionUser;
import com.yoogurt.taxi.common.constant.CacheKey;
import com.yoogurt.taxi.common.helper.RedisHelper;
import com.yoogurt.taxi.common.vo.RestResult;
import com.yoogurt.taxi.dal.model.user.AuthorityModel;
import com.yoogurt.taxi.gateway.rest.AuthorityService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;

import java.util.List;

/**
 * Description:
 * 用于实现shiro的鉴权（Authentication）和授权（Authorization）。
 * @Author Eric Lau
 * @Date 2017/9/5.
 */
public class ShiroRealm extends AuthorizingRealm{

    @Autowired
    private RedisHelper redisHelper;

    @Autowired
    private AuthorityService authorityService;

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
        final Long userId = Long.valueOf(principals.getPrimaryPrincipal().toString());
        final RestResult<List<AuthorityModel>> obj = authorityService.getAuthoritiesByUserId(userId);
        if (obj.getBody() != null) {
            List<AuthorityModel> authorities = obj.getBody();
            if (CollectionUtils.isNotEmpty(authorities)) {
                authorities.forEach(authority -> {
                    authorizationInfo.addRole(authority.getRoleName());
                    authorizationInfo.addStringPermission(authority.getUri());
                });
            }
        }
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
            Object obj = redisHelper.get(CacheKey.GRANT_CODE_KEY + token.getUserId());
            //grantCode不存在，或者已失效
            if (obj == null || !obj.toString().equals(token.getGrantCode())) return null;

            Long userId = token.getUserId();
            String from = token.getFrom();
            String username = token.getUsername();

//            Integer userType = token.getUserType();

            Object o = redisHelper.getObject(CacheKey.SESSION_USER_KEY + userId);
            if (o == null || StringUtils.isBlank(token.getToken())) return null;
            SessionUser user = (SessionUser) o;
            //设置token
            user.setToken(token.getToken());
            //设置SessionUser
            redisHelper.setObject(CacheKey.SESSION_USER_KEY + userId, user);
            //填充principals，第一个add进去的即为PrimaryPrincipal
            SimplePrincipalCollection principals = new SimplePrincipalCollection();
            //UserId为PrimaryPrincipal，可直接使用Subject.getPrincipal()获取
            principals.add(userId, "UserId");
            principals.add(username, "UserName");
            principals.add(from, "From");
//            principals.add(userType, "userType");
            principals.add(user, "UserInfo");
            //userId和username拼接，MD5加密，作为shiro中的临时密码
            String credentials = DigestUtils.md5DigestAsHex((userId + username).getBytes());
            //将临时密码设置到token中，shiro会将token中的password和AuthenticationInfo中的credentials进行匹配
            token.setPassword(credentials.toCharArray());
            return new SimpleAuthenticationInfo(principals, credentials, ByteSource.Util.bytes(credentials));
        }
        return null;
    }

    /**
     * 重写方法，将userId作为一部分拼接在redis key后面
     * @param principals 凭证
     * @return principals.getPrimaryPrincipal()
     */
    @Override
    protected Object getAuthorizationCacheKey(PrincipalCollection principals) {

        return principals.getPrimaryPrincipal().toString();
    }
}
