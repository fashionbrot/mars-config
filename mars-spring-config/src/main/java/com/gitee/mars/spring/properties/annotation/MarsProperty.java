package com.gitee.mars.spring.properties.annotation;


import java.lang.annotation.*;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 *
 * An annotation for mars Property name of  mars Configuration to bind a field from
 * annotated {@link  MarsConfigurationProperties} Properties Object.
 *
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MarsProperty {


    /**
     * The property name of mars Configuration to bind a field
     *
     * @return property name
     */
    String value();
}
