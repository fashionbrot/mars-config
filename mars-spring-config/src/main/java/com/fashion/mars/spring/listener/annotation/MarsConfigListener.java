package com.fashion.mars.spring.listener.annotation;


import com.fashion.mars.spring.enums.ConfigTypeEnum;

import java.lang.annotation.*;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 *
 * Annotation that marks a method as a listener for mars Config change.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface MarsConfigListener {

    /**
     * listener filename
     * @return
     */
    String fileName();

    /**
     * support properties yml text
     * @see com.fashion.mars.spring.enums.ConfigTypeEnum
     * @return
     */
    ConfigTypeEnum type() default ConfigTypeEnum.PROPERTIES;
}
