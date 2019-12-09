package com.fashion.mars.spring.properties.config;

import com.alibaba.fastjson.JSON;
import com.fashion.mars.spring.enums.ConfigTypeEnum;
import com.fashion.mars.spring.event.MarsListenerEvent;
import com.fashion.mars.spring.properties.annotation.MarsConfigurationProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 *
 */
@Slf4j
public class MarsConfigurationPropertiesBindingPostProcessor implements BeanPostProcessor, ApplicationContextAware,
        ApplicationListener<MarsListenerEvent> {

    public static final String BEAN_NAME = "marsConfigurationPropertiesBindingPostProcessor";

    private ConfigurableApplicationContext applicationContext;

    private Map<String,MarsConfigurationTarget> configurationTargetMap =new ConcurrentHashMap<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {


        MarsConfigurationProperties configurationProperties = findAnnotation(bean.getClass(), MarsConfigurationProperties.class);

        if (configurationProperties != null) {
            bind(bean, beanName, configurationProperties);

            if (configurationProperties.autoRefreshed()){
                configurationTargetMap.put(configurationProperties.fileName(),
                        MarsConfigurationTarget.builder()
                                .bean(bean)
                                .beanName(beanName)
                                .marsConfigurationProperties(configurationProperties)
                                .build()
                        );
            }
        }

        return bean;
    }

    private void bind(Object bean, String beanName, MarsConfigurationProperties configurationProperties) {

        MarsConfigurationPropertiesBinder binder;
        try {
            binder = applicationContext
                    .getBean(MarsConfigurationPropertiesBinder.BEAN_NAME, MarsConfigurationPropertiesBinder.class);
            if (binder == null) {
                binder = new MarsConfigurationPropertiesBinder(applicationContext);
            }

        } catch (Exception e) {
            binder = new MarsConfigurationPropertiesBinder(applicationContext);
        }

        binder.bind(bean, beanName, configurationProperties);

    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    @Override
    public void onApplicationEvent(MarsListenerEvent event) {
        if (event==null){
            log.info("onApplicationEvent event is null ");
            return;
        }
        if(ConfigTypeEnum.TEXT == event.getDataConfig().getConfigType()){
            return;
        }
        String fileName = event.getDataConfig().getFileName();
        MarsConfigurationTarget target= configurationTargetMap.get(fileName);
        if (target==null){
            if (log.isDebugEnabled()){
                log.debug("onApplicationEvent fileName:{} marsConfigurationProperties autoRefreshed false ",fileName);
            }
            return;
        }
        if (log.isInfoEnabled()){
            log.info("onApplicationEvent marsConfigurationReceivedEvent:\n{} ", JSON.toJSONString(event) );
        }
        bind(target.getBean(),target.getBeanName(),target.getMarsConfigurationProperties());
    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    private static class MarsConfigurationTarget {

        private Object bean;

        private String beanName;

        private MarsConfigurationProperties marsConfigurationProperties;
    }
}
