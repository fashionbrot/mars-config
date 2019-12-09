package com.fashion.mars.spring.properties.annotation;

import java.lang.annotation.*;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 *
 * An annotation for ignore field from annotated {@link MarsConfigurationProperties} Properties Object.
 *
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MarsIgnoreField {

}
