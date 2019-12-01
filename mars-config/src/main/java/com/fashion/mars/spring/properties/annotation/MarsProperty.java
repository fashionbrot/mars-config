package com.fashion.mars.spring.properties.annotation;


import java.lang.annotation.*;

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
