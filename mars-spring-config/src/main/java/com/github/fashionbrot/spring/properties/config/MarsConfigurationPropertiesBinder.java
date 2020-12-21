package com.github.fashionbrot.spring.properties.config;

import com.github.fashionbrot.ribbon.util.CollectionUtil;
import com.github.fashionbrot.spring.api.ApiConstant;
import com.github.fashionbrot.spring.env.MarsPropertySource;
import com.github.fashionbrot.spring.properties.annotation.MarsConfigurationProperties;
import com.github.fashionbrot.spring.util.MarsUtil;
import com.github.fashionbrot.spring.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.PropertyValues;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.Assert;
import org.springframework.validation.DataBinder;
import java.util.Map;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 *
 */
@Slf4j
public class MarsConfigurationPropertiesBinder {

    public static final String BEAN_NAME = "marsConfigurationPropertiesBinder";


    private final ConfigurableApplicationContext applicationContext;

    private  ConfigurableEnvironment  environment;

    private final ApplicationEventPublisher applicationEventPublisher;


    public MarsConfigurationPropertiesBinder(ConfigurableApplicationContext applicationContext) {
        Assert.notNull(applicationContext, "ConfigurableApplicationContext must not be null!");
        this.applicationContext = applicationContext;
        this.environment = applicationContext.getEnvironment();
        this.applicationEventPublisher = applicationContext;
    }

    protected void bind(Object bean, String beanName) {

        MarsConfigurationProperties marsConfigurationProperties = findAnnotation(bean.getClass(), MarsConfigurationProperties.class);
        if (marsConfigurationProperties==null){
            return;
        }

        bind(bean, beanName, marsConfigurationProperties);

    }

    public void bind(final Object bean, final String beanName, final MarsConfigurationProperties properties) {

        Assert.notNull(bean, "Bean must not be null!");

        Assert.notNull(properties, "marsConfigurationProperties must not be null!");

        MarsPropertySource marsPropertySource = (MarsPropertySource) environment.getPropertySources().get(ApiConstant.NAME+properties.fileName());
        if (marsPropertySource!=null){
            doBind(bean, properties, marsPropertySource.getSource());
        }
    }

    private void doBind(Object bean,MarsConfigurationProperties properties, Map<String,Object> source) {
        if (CollectionUtil.isNotEmpty(source)) {
            PropertyValues propertyValues = MarsUtil.resolvePropertyValues(bean, properties.prefix(), source);
            bindBean(bean, properties, propertyValues);
        }
    }

    private void bindBean(Object bean, MarsConfigurationProperties properties, PropertyValues propertyValues) {
        ObjectUtils.cleanMapOrCollectionField(bean);
        DataBinder dataBinder = new DataBinder(bean);
        dataBinder.setIgnoreInvalidFields(properties.ignoreInvalidFields());
        dataBinder.setIgnoreUnknownFields(properties.ignoreUnknownFields());
        dataBinder.setAutoGrowNestedPaths(properties.autoGrowNestedPaths());
        dataBinder.bind(propertyValues);
    }

}
