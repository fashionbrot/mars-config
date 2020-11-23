package com.github.fashionbrot.value.config.annotation;

import com.github.fashionbrot.value.config.MarsValueBeanDefinitionRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2020/11/23
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MarsValueBeanDefinitionRegistrar.class)
public @interface EnableMarsValue {

    String APP_ID = "${mars.value.app-id}";

    String ENV_CODE = "${mars.value.env-code}";

    String SERVER_ADDRESS = "${mars.value.http.server-address}";

    /**
     * Local cache file path
     */
    String LOCAL_CACHE_PATH = "${mars.value.local-cache-path}";


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
     * Local cache file path
     * @return
     */
    String localCachePath() default LOCAL_CACHE_PATH;
}
