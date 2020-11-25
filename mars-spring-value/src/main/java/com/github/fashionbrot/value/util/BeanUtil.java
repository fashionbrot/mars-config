package com.github.fashionbrot.value.util;

import com.github.fashionbrot.ribbon.util.StringUtil;
import com.github.fashionbrot.value.GlobalMarsValueProperties;
import com.github.fashionbrot.value.MarsConfigValue;
import com.github.fashionbrot.value.event.ConfigPostProcessor;
import com.github.fashionbrot.value.event.HttpBeanPostProcessor;
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
import java.util.Map;
import java.util.Properties;

import static org.springframework.util.SystemPropertyUtils.PLACEHOLDER_PREFIX;
import static org.springframework.util.SystemPropertyUtils.PLACEHOLDER_SUFFIX;


@Slf4j
public class BeanUtil {


    public static void registerGlobalMarsValueProperties(AnnotationAttributes attributes, BeanDefinitionRegistry registry, PropertyResolver environment, ConfigurableListableBeanFactory beanFactory) {

        Properties globalProperties = resolveProperties(attributes, environment);

        GlobalMarsValueProperties globalMarsProperties = GlobalMarsValueProperties.builder()
                .appName(getProperties(globalProperties, "appId"))
                .envCode(getProperties(globalProperties, "envCode"))
                .serverAddress(getProperties(globalProperties, "serverAddress"))
                .listenLongPollMs(StringUtil.parseLong(getProperties(globalProperties, "listenLongPollMs"), 30000L))
                .localCachePath(getProperties(globalProperties,"localCachePath"))
                .enableListenLog(Boolean.parseBoolean(getProperties(globalProperties,"enableListenLog")))
                .build();
        registerSingleton(registry, GlobalMarsValueProperties.BEAN_NAME, globalMarsProperties);
    }

    public static void registerConfigPostProcessor(BeanDefinitionRegistry registry) {
        registerInfrastructureBeanIfAbsent(registry, ConfigPostProcessor.BEAN_NAME, ConfigPostProcessor.class);
    }

    public static void registerMarsConfigValue(BeanDefinitionRegistry registry) {
        registerInfrastructureBeanIfAbsent(registry, MarsConfigValue.BEAN_NAME, MarsConfigValue.class);
    }

    public static void registerHttpBeanPostProcessor(BeanDefinitionRegistry registry) {
        registerInfrastructureBeanIfAbsent(registry, HttpBeanPostProcessor.BEAN_NAME, HttpBeanPostProcessor.class);
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
