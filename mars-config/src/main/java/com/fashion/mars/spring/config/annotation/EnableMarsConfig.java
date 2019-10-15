package com.fashion.mars.spring.config.annotation;

import com.fashion.mars.spring.config.MarsConfigBeanDefinitionRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MarsConfigBeanDefinitionRegistrar.class)
public @interface EnableMarsConfig {

    String APP_ID = "${mars.config.app-id}";

    String ENV_CODE = "${mars.config.env-code}";

    String SERVER_ADDRESS = "${mars.config.http.server-address}";

    String appId() default APP_ID;

    String envCode() default ENV_CODE;

    String serverAddress() default SERVER_ADDRESS;
}
