package com.yoogurt.taxi.dal.annotation;

import java.lang.annotation.*;

/**
 * 使用AOP对model中的公共字段进行注入，
 * 必须在类上添加此注解。
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Domain {

}