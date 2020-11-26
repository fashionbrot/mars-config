package com.github.fashionbrot.spring.util;

import com.github.fashionbrot.ribbon.util.CollectionUtil;
import com.github.fashionbrot.ribbon.util.StringUtil;
import com.github.fashionbrot.spring.config.GlobalMarsProperties;
import com.github.fashionbrot.spring.context.ApplicationContextHolder;
import com.github.fashionbrot.spring.env.MarsPropertySourcePostProcessor;
import com.github.fashionbrot.spring.event.MarsTimerHttpBeanPostProcessor;
import com.github.fashionbrot.spring.listener.MarsConfigListenerMethodProcessor;
import com.github.fashionbrot.spring.properties.config.MarsConfigurationPropertiesBindingPostProcessor;
import com.github.fashionbrot.spring.value.MarsValueAnnotationBeanPostProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertyResolver;

import java.lang.reflect.Constructor;
import java.util.*;

import static org.springframework.util.SystemPropertyUtils.PLACEHOLDER_PREFIX;
import static org.springframework.util.SystemPropertyUtils.PLACEHOLDER_SUFFIX;


@Slf4j
public class BeanUtil {


    public static void registerGlobalMarsProperties(AnnotationAttributes attributes, BeanDefinitionRegistry registry, PropertyResolver environment, ConfigurableListableBeanFactory beanFactory) {
        GlobalMarsProperties globalMarsProperties = null;
        if (CollectionUtil.isNotEmpty(attributes)){
            Properties globalProperties = resolveProperties(attributes, environment);

             globalMarsProperties = GlobalMarsProperties.builder()
                    .appName(getProperties(globalProperties, "appId"))
                    .envCode(getProperties(globalProperties, "envCode"))
                    .serverAddress(getProperties(globalProperties, "serverAddress"))
                    .listenLongPollMs(StringUtil.parseLong(getProperties(globalProperties, "listenLongPollMs"), 30000L))
                    .enableLocalCache(StringUtil.parseBoolean(getProperties(globalProperties,"enableLocalCache"),false))
                    .localCachePath(getProperties(globalProperties,"localCachePath"))
                    .build();
        }else{
            globalMarsProperties = GlobalMarsProperties.builder()
                    .appName(getEnvValue(environment, "mars.config.app-id",""))
                    .envCode(getEnvValue(environment, "mars.config.env-code",""))
                    .serverAddress(getEnvValue(environment, "mars.config.server-address",""))
                    .listenLongPollMs(StringUtil.parseLong(getEnvValue(environment, "mars.config.listen-long-poll-ms", "50000"),50000L))
                    .enableLocalCache(StringUtil.parseBoolean(getEnvValue(environment,"mars.config.enable-local-cache","false"),false))
                    .localCachePath(getEnvValue(environment,"mars.config.local-cache-path",""))
                    .build();
        }

        registerSingleton(registry, GlobalMarsProperties.BEAN_NAME, globalMarsProperties);
    }

    private static String getEnvValue(PropertyResolver environment,String key,String defaultValue){
        if (environment!=null && environment.containsProperty(key)){
            String value = environment.getProperty(key);
            if (StringUtil.isEmpty(value)){
                value = defaultValue;
            }
            return value;
        }
        return "";
    }


    public static void registerMarsConfigurationPropertiesBindingPostProcessor(BeanDefinitionRegistry registry) {
        registerInfrastructureBeanIfAbsent(registry, MarsConfigurationPropertiesBindingPostProcessor.BEAN_NAME, MarsConfigurationPropertiesBindingPostProcessor.class);
    }

    public static void registerApplicationContextHolder(BeanDefinitionRegistry registry) {
        // Register applicationContextHolder Bean
        registerInfrastructureBeanIfAbsent(registry, ApplicationContextHolder.BEAN_NAME, ApplicationContextHolder.class);
    }

    public static void registerMarsPropertySourcePostProcessor(BeanDefinitionRegistry registry) {
        registerInfrastructureBeanIfAbsent(registry, MarsPropertySourcePostProcessor.BEAN_NAME, MarsPropertySourcePostProcessor.class);
    }

    public static void registerMarsTimerHttpBeanPostProcessor(BeanDefinitionRegistry registry) {
        registerInfrastructureBeanIfAbsent(registry, MarsTimerHttpBeanPostProcessor.BEAN_NAME, MarsTimerHttpBeanPostProcessor.class);
    }


    /**
     * Resolve placeholders of properties via specified {@link PropertyResolver} if present
     *
     * @param properties       The properties
     * @param propertyResolver {@link PropertyResolver} instance, for instance, {@link Environment}
     * @return a new instance of {@link Properties} after resolving.
     */
    public static Properties resolveProperties(Map<?, ?> properties, PropertyResolver propertyResolver) {
        PropertiesPlaceholderResolver propertiesPlaceholderResolver = new PropertiesPlaceholderResolver(propertyResolver);
        return propertiesPlaceholderResolver.resolve(properties);
    }

    /**
     * Register an object to be Singleton Bean
     *
     * @param registry        {@link BeanDefinitionRegistry}
     * @param beanName        bean name
     * @param singletonObject singleton object
     */
    public static void registerSingleton(BeanDefinitionRegistry registry, String beanName, Object singletonObject) {
        SingletonBeanRegistry beanRegistry = null;
        if (registry instanceof SingletonBeanRegistry) {
            beanRegistry = (SingletonBeanRegistry) registry;
        } else if (registry instanceof AbstractApplicationContext) {
            // Maybe AbstractApplicationContext or its sub-classes
            beanRegistry = ((AbstractApplicationContext) registry).getBeanFactory();
        }
        // Register Singleton Object if possible
        if (beanRegistry != null) {
            beanRegistry.registerSingleton(beanName, singletonObject);
        }
    }

    public static Object getSingleton(BeanFactory registry, String beanName) {
        SingletonBeanRegistry beanRegistry = null;
        if (registry instanceof SingletonBeanRegistry) {
            beanRegistry = (SingletonBeanRegistry) registry;
        } else if (registry instanceof AbstractApplicationContext) {
            // Maybe AbstractApplicationContext or its sub-classes
            beanRegistry = ((AbstractApplicationContext) registry).getBeanFactory();
        }
        if (beanRegistry != null) {
            return beanRegistry.getSingleton(beanName);
        }
        return null;
    }


    public static void registerMarsValueAnnotationBeanPostProcessor(BeanDefinitionRegistry registry) {
        registerInfrastructureBeanIfAbsent(registry, MarsValueAnnotationBeanPostProcessor.BEAN_NAME,
                MarsValueAnnotationBeanPostProcessor.class);
    }

    public static void registerMarsListener(BeanDefinitionRegistry registry) {
        registerInfrastructureBeanIfAbsent(registry, MarsConfigListenerMethodProcessor.BEAN_NAME, MarsConfigListenerMethodProcessor.class);
    }

    /**
     * Register Infrastructure Bean if absent
     *
     * @param registry        {@link BeanDefinitionRegistry}
     * @param beanName        the name of bean
     * @param beanClass       the class of bean
     * @param constructorArgs the arguments of {@link Constructor}
     */
    public static void registerInfrastructureBeanIfAbsent(BeanDefinitionRegistry registry, String beanName, Class<?> beanClass,
                                                          Object... constructorArgs) {
        if (!registry.containsBeanDefinition(beanName)) {
            registerInfrastructureBean(registry, beanName, beanClass, constructorArgs);
        }
    }

    /**
     * Register Infrastructure Bean
     *
     * @param registry        {@link BeanDefinitionRegistry}
     * @param beanName        the name of bean
     * @param beanClass       the class of bean
     * @param constructorArgs the arguments of {@link Constructor}
     */
    public static void registerInfrastructureBean(BeanDefinitionRegistry registry, String beanName, Class<?> beanClass,
                                                  Object... constructorArgs) {
        // Build a BeanDefinition for serviceFactory class
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(beanClass);
        for (Object constructorArg : constructorArgs) {
            beanDefinitionBuilder.addConstructorArgValue(constructorArg);
        }
        // ROLE_INFRASTRUCTURE
        beanDefinitionBuilder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        // Register
        registry.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
    }



    public static String getProperties(Properties properties, String key) {

        String value = properties.containsKey(key) ? properties.getProperty(key) : "";
        if (ObjectUtils.isNotEmpty(value) && value.startsWith(PLACEHOLDER_PREFIX)) {
            return null;
        }
        if (ObjectUtils.isNotEmpty(value) && value.endsWith(PLACEHOLDER_SUFFIX)) {
            return null;
        }
        return value;
    }


}
