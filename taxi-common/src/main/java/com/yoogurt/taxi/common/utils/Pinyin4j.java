package com.yoogurt.taxi.common.utils;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang3.StringUtils;

/**
 * Description:
 * 汉字转拼音，支持词库导入
 * @author Eric Lau
 * @Date 2017/5/25.
 */
@Slf4j
public class Pinyin4j {

    /**
     * 拼音转换方法。
     * @param content 待转换的内容
     * @param uppercase 是否返回大写拼音字母
     * @param getFirstLetter 是否获取首位拼音字母
     * @return 转换成拼音的字符串
     */
    public static String getPYName(String content, boolean uppercase, boolean getFirstLetter) {
        //加载词库
//        MultiPinyinConfig.multiPinyinPath = "pinyindb/multi_pinyin.txt";

        try {
            if (StringUtils.isNoneBlank(content)) {

                HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
                //单个字转换后的字母后面会带上声调，此处设置是去掉声调
                outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
                //设置转换的字母大小写
                outputFormat.setCaseType(uppercase ? HanyuPinyinCaseType.UPPERCASE : HanyuPinyinCaseType.LOWERCASE);
                //将 ü 输出为 v
                outputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
                if (getFirstLetter){
                    return PinyinHelper.toHanYuPinyinStringFirstLetter(content, outputFormat, "", true);
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            log.error("汉字转拼音出现异常{}", e);
        }

        return StringUtils.EMPTY;
    }
}
