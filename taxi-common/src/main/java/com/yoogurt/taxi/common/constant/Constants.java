package com.yoogurt.taxi.common.constant;

import com.yoogurt.taxi.common.bo.Money;

import java.math.BigDecimal;

/**
 * Description:
 * 常量
 * @Author Eric Lau
 * @Date 2017/8/31.
 */
public final class Constants {

    /**
     * 密码试错最大次数
     */
    public static final int PASSWORD_RETRY_MAX_COUNT = 5;

    /**
     * 重置密码试错次数的间隔时间，单位：秒
     */
    public static final int PASSWORD_RETRY_INTERVAL_SECONDS = 1800;

    /**
     * 最大间隔天数
     */
    public static final int MAX_INTERVAL_DAYS = 20;

    /**
     * 验证码有效时间3*60s
     */
    public static final int VERIFY_CODE_EXPIRE_SECONDS = 300;

    /**
     * 交车时间与发单时间的间隔时间
     */
    public static final int MIN_PUBLISH_INTERVAL_HOURS = 1;

    /**
     * 交车时间与还车时间的间隔时间
     */
    public static final int MIN_WORKING_HOURS = 8;

    /**
     * 发布的租单，交车时间和还车时间的间隔时长
     */
    public static final int MIN_HOURS = 8;

    /**
     * 允许最大的发单数量
     */
    public static final int MAX_RENT_COUNT = 5;

    /**
     * 为了保证API的幂等性，每次请求需要有一个唯一的REQUEST_ID，
     * 所以加一个自定义Header：yoogurt-request-id: <request_id>。
     * 系统中用到的request_id生成算法暂定为UUID。
     * 对于只读操作的API，request_id不强制性要求传入，涉及到对系统数据有更改的操作，则是必传的！
     */
    public static final String REQUEST_ID_HEADER_NAME = "X-yoogurt-request-id";

    /**
     * 标识App的用户类型
     * (20,"代理端用户"), (30,"正式端用户"),
     */
    public static final String USER_TYPE_HERDER_NAME = "X-yoogurt-user-type";

    public static final String REQUEST_SYSTEM_TYPE = "X-yoogurt-system-type";

    /**
     * 授权码的有效期，单位：秒
     */
    public static final int GRANT_CODE_EXPIRE_SECONDS = 60;

    /**
     * 授权码的长度
     */
    public static final int GRANT_CODE_LENGTH = 6;

    /** unknown */
    public static final String UNKNOWN = "unknown";

    /** 文件后缀分割符（占用1个字符） */
    public static final String FILE_SUFFIX_SPLIT_MARK = "\\.";

    /** excel后缀 */
    public static final String EXCEL_FILE_SUFFIX_X    = "xlsx";

    /** excel后缀 */
    public static final String EXCEL_FILE_SUFFIX      = "xls";

    /** jpg后缀 */
    public static final String JPG_FILE_SUFFIX        = "jpg";

    /** jpeg后缀 */
    public static final String JPEG_FILE_SUFFIX       = "jpeg";

    /** png后缀 */
    public static final String PNG_FILE_SUFFIX        = "png";

    /** ico后缀 */
    public static final String ICO_FILE_SUFFIX        = "ico";

    /** ico后缀 */
    public static final String GIF_FILE_SUFFIX        = "gif";

    /** 代理司机应收押金 */
    public static final BigDecimal AGENT_RECEIVABLE_DEPOSIT = new Money("3000").getAmount();

    /** 正式司机应收押金 */
    public static final BigDecimal OFFICE_RECEIVABLE_DEPOSIT = new Money("1000").getAmount();

    /** 替你开司机端 */
    public static final String AGENT_APP_NAME = "替你开司机端";

    /** 替你开车主端 */
    public static final String OFFICIAL_APP_NAME = "替你开车主端";

    /**提现允许日期*/
    public static final int withdraw_day_of_week = 2;
    /**提现允许开始时间*/
    public static final String withdraw_start_time = "9:00";
    /**提现允许结束时间*/
    public static final String withdraw_end_time = "18:00";
    /**提现允许次数*/
    public static final int withdraw_times = 1;

}
