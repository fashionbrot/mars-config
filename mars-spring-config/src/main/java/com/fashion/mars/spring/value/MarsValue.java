package com.fashion.mars.spring.value;

import java.lang.annotation.*;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 *
 * Annotation which extends value to support auto-refresh
 *
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MarsValue {

    /**
     * The actual value expression: e.g. "#{systemProperties.myProp}".
     * @return
     */
    String value();

    /**
     * It indicates that the currently bound property is auto-refreshed when mars configuration is changed.
     * @return
     */
    boolean autoRefreshed() default false;

}