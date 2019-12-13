package com.github.fashionbrot.common.annotation;

import java.lang.annotation.*;

@Documented
@Target({ ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IsMenu {

    /**
     * 是否检测 url 有权限
     * @return
     */
    boolean checkMenuUrlPermission() default true;

}
