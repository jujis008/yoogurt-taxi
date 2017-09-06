package com.yoogurt.taxi.common.constant;

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
     * 半小时内重置密码试错次数
     */
    public static final int PASSWORD_RETRY_INTERVAL_SECONDS = 1800;

    /** unknown */
    public static final String UNKNOWN = "unknown";

    /** 文件后缀分割符（占用1个字符） */
    public static final String FILE_SUFFIX_SPLIT_MARK = "\\.";

    /** excel后缀 */
    public static final String EXCELX_FILE_SUFFIX     = "xlsx";

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
}
