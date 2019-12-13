package com.github.fashionbrot.spring.config;

import com.github.fashionbrot.spring.config.annotation.EnableMarsConfig;
import com.github.fashionbrot.spring.util.BeanUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import static com.github.fashionbrot.spring.util.BeanUtil.*;
import static org.springframework.core.annotation.AnnotationAttributes.fromMap;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */
public class MarsConfigBeanDefinitionRegistrar  implements ImportBeanDefinitionRegistrar, EnvironmentAware, BeanFactoryAware {



    private Environment environment;

    private ConfigurableListableBeanFactory beanFactory;


    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        Assert.isInstanceOf(ConfigurableListableBeanFactory.class, beanFactory, "MarsConfigBeanDefinitionRegistrar requires a ConfigurableListableBeanFactory");
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {


        AnnotationAttributes attributes = fromMap(metadata.getAnnotationAttributes(EnableMarsConfig.class.getName()));

        /**
         * Register http Global mars Properties Bean
         */
        registerGlobalMarsProperties(attributes, registry, environment,beanFactory);
        /**
         * Register applicationContextHolder Bean
         */
        BeanUtil.registerApplicationContextHolder(registry);

        /**
         * register MarsConfigurationPropertiesBindingPostProcessor bean
         */
        registerMarsConfigurationPropertiesBindingPostProcessor(registry);
        /**
         * register MarsValue config bean
         */
        registerMarsValueAnnotationBeanPostProcessor(registry);

        /**
         * register listenerPostProcessor bean
         */
        registerMarsListener(registry);

        registerMarsPropertySourcePostProcessor(registry);

        registerMarsTimerHttpBeanPostProcessor(registry);
    }


}
