package com.github.fashionbrot.spring.listener;

import com.github.fashionbrot.spring.api.ApiConstant;
import com.github.fashionbrot.spring.config.MarsDataConfig;
import com.github.fashionbrot.spring.enums.ConfigTypeEnum;
import com.github.fashionbrot.spring.env.MarsPropertySource;
import com.github.fashionbrot.spring.event.MarsListenerEvent;
import com.github.fashionbrot.spring.listener.annotation.MarsConfigListener;

import com.github.fashionbrot.spring.util.PropertiesSourceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fashionbrot
 * @version 0.1.1
 * @date 2019/12/8 22:45
 *
 */
@Slf4j
public class MarsConfigListenerMethodProcessor extends AbstractAnnotationListenerMethodProcessor<MarsConfigListener>
        implements ApplicationListener<MarsListenerEvent>, EnvironmentAware {

    private static Map<String,MarsListenerSourceTarget> targetMap = new ConcurrentHashMap<>();

    public static final String BEAN_NAME = "MarsConfigListenerMethodProcessor";

    private ConfigurableEnvironment environment;

    @Override
    protected void processListenerMethod(String beanName, final Object bean, Class<?> beanClass,
                                         final MarsConfigListener listener, final Method method) {


        String fileName =  listener.fileName();
        Assert.isTrue(StringUtils.hasText(fileName), "fileName must have content");

        MarsListenerSourceTarget target = MarsListenerSourceTarget.builder()
                .bean(bean)
                .method(method)
                .listener(listener)
                .build();
        targetMap.put(fileName,target);

        invokeMethod(target);

    }

    private void invokeMethod(MarsListenerSourceTarget target){
        String fileName = target.getListener().fileName();
        ConfigTypeEnum type = ConfigTypeEnum.PROPERTIES;
        Class<?>[] parameterTypes = target.getMethod().getParameterTypes();
        if (parameterTypes.length!=1){
            log.error(" processListenerMethod invokeMethod target method parameterType can not be empty ");
            return;
        }
        if (parameterTypes[0]  == String.class){
            type =  ConfigTypeEnum.TEXT;
        }

        MarsPropertySource marsPropertySource = (MarsPropertySource) environment.getPropertySources().get(ApiConstant.NAME+target.getListener().fileName());
        if (marsPropertySource!=null) {
            try {
                MarsDataConfig marsDataConfig =  marsPropertySource.getMarsDataConfig();
                if (marsDataConfig!=null) {
                    String content = marsDataConfig.getContent();
                    if (type == ConfigTypeEnum.TEXT) {
                        ReflectionUtils.invokeMethod(target.getMethod(), target.getBean(), content);
                    } else if (type == ConfigTypeEnum.PROPERTIES) {
                        Properties p = PropertiesSourceUtil.toProperties(content, ConfigTypeEnum.PROPERTIES);
                        ReflectionUtils.invokeMethod(target.getMethod(), target.getBean(), p);
                    } else if (type == ConfigTypeEnum.YAML) {
                        Properties p = PropertiesSourceUtil.toProperties(content, ConfigTypeEnum.YAML);
                        ReflectionUtils.invokeMethod(target.getMethod(), target.getBean(), p);
                    }
                }
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error("invokeMethod can't add Listener for fileName: {} error:{} ", fileName, e);
                }
            }
        }
    }

    @Override
    public void onApplicationEvent(MarsListenerEvent marsListenerEvent) {
        MarsListenerSourceTarget target =targetMap.get(marsListenerEvent.getDataConfig().getFileName());
        if (target!=null && target.getListener().autoRefreshed()){
            invokeMethod(target);
        }
    }


    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }
}
