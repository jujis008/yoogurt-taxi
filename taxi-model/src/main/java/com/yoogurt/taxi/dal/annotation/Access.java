package com.yoogurt.taxi.dal.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Access {
    String value() default "";
    String tableName() default "";
    String description() default "";
}
