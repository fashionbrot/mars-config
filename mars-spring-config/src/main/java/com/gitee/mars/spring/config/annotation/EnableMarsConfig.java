package com.gitee.mars.spring.config.annotation;

import com.gitee.mars.spring.config.MarsConfigBeanDefinitionRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MarsConfigBeanDefinitionRegistrar.class)
public @interface EnableMarsConfig {

    String APP_ID = "${mars.config.app-id}";

    String ENV_CODE = "${mars.config.env-code}";

    String SERVER_ADDRESS = "${mars.config.http.server-address}";

    /**
     * project name
     * @return
     */
    String appId() default APP_ID;

    /**
     * profiles env code
     * @return
     */
    String envCode() default ENV_CODE;

    /**
     * mars server address
     * @return
     */
    String serverAddress() default SERVER_ADDRESS;

    /**
     * listen long poll timeout  default 30000 ms
     */
    String listenLongPollMs() default "30000";

    /**
     * listen long poll log enabled
     */
    boolean listenLongPollLogEnabled() default false;
}
