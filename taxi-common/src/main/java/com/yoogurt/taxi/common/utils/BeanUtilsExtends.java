package com.yoogurt.taxi.common.utils;

import org.apache.commons.beanutils.ConvertUtils;
import org.springframework.beans.BeanUtils;

/**
 *拓展beanutils的Java.util.Date不能复制问题
 */
public class BeanUtilsExtends extends BeanUtils {
    static {
        ConvertUtils.register(new DateConvert(), java.util.Date.class);
        ConvertUtils.register(new DateConvert(), java.sql.Date.class);
    }

    public static void copyProperties(Object dest, Object orig) {
            BeanUtils.copyProperties(orig, dest);
    }

}
