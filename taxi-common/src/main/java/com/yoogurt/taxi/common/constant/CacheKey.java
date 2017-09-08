package com.yoogurt.taxi.common.constant;

/**
 * Description:
 * Redis专用的常量类。
 * 命名有个基本的规范：
 * ************************************************************************
 * <p>
 * key name 一律使用大写，并添加必要的注释。
 * 修饰符：public static final。
 * 类型：{@link String}
 * </p>
 * -------------------------------------------------------------------------
 * <p>
 * 如果是用于临时作用的key，需要在key name前追加 T_，形如 {@code <T_CODE_KEY>};
 * 如果是相对持久的缓存作用的key，需要在key name前追加 P_，形如 {@code <P_CODE_KEY>};
 * 如果是定时过期的key，需要在key name前追加 E_，形如 {@code <E_CODE_KEY>}。
 * </p>
 * -------------------------------------------------------------------------
 * <p>
 * 如果key name后需拼接内容，使用#分隔，形如{@code <T_CODE_KEY#userId> }。
 * </p>
 * -------------------------------------------------------------------------
 * <p>
 * 末尾拼接 _KEY，表示这是一个key name。
 * 末尾拼接 _MAP，表示这是一个 map 对象。
 * </p>
 * **************************************************************************
 * @author Eric Lau
 * @Date 2017/9/6.
 */
public final class CacheKey {

    /**
     * 客户端登录成功，获取授权码。
     * 凭此授权码，获取系统颁发的token。
     * 成功生成token，并返回给客户端，将清除grant_code缓存。
     */
    public static final String GRANT_CODE_KEY = "E_GRANT_CODE#";

    /**
     * 缓存{@link com.yoogurt.taxi.common.bo.SessionUser} SessionUser对象。
     * 后面拼接userId.
     */
    public static final String SESSION_USER_KEY = "P_SESSION_USER#";

    /**
     * 客户端通过授权码，获取到token，拼接userId。
     */
    public static final String TOKEN_KEY = "P_TOKEN#";

    /**
     * 用于缓存用户密码试错的次数，后面拼接username。
     */
    public static final String PASSWORD_RETRY_MAX_COUNT_KEY = "E_PASSWORD_RETRY_MAX_COUNT#";

    /**
     * 短信验证码，后面拼接手机号码
     */
    public static final String VERIFY_CODE_KEY = "E_VERIFY_CODE#";
}
