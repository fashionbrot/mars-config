package com.fashion.mars.spring.listener.annotation;


import com.fashion.mars.spring.enums.ConfigTypeEnum;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface MarsConfigListener {

    /**
     * filename
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
