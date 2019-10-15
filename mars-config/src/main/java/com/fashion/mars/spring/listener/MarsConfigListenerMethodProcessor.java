package com.fashion.mars.spring.listener;

import com.fashion.mars.spring.enums.ConfigTypeEnum;
import com.fashion.mars.spring.event.MarsListenerEvent;
import com.fashion.mars.spring.listener.annotation.MarsConfigListener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class MarsConfigListenerMethodProcessor extends AnnotationListenerMethodProcessor<MarsConfigListener>
        implements ApplicationListener<MarsListenerEvent>  {

    private static Map<String,MarsListenerSourceTarget> targetMap = new ConcurrentHashMap<>();

    public static final String BEAN_NAME = "MarsConfigListenerMethodProcessor";


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
        ConfigTypeEnum type = target.getListener().type();
        Class<?>[] parameterTypes = target.getMethod().getParameterTypes();
        if (parameterTypes.length!=1){
            log.error(" processListenerMethod invokeMethod target method parameterType can not be empty ");
            return;
        }
        Class parameterType = Properties.class ;
        if (target.getListener().type() == ConfigTypeEnum.TEXT){
            parameterType =  String.class;
        }
        if (parameterTypes[0] != parameterType){
            log.error(" processListenerMethod invokeMethod target method parameterType Parameter type mismatch ");
            return;
        }
        /*try {
            ForDataVo dataVo = FileCache.getCache(fileName);
            if (dataVo != null) {
                if (type == ConfigTypeEnum.TEXT) {
                    ReflectionUtils.invokeMethod(target.getMethod(), target.getBean(), dataVo.getContent());
                } else if (type == ConfigTypeEnum.PROPERTIES) {
                    Properties p = ConfigParseUtils.toProperties(dataVo.getContent(), ConfigTypeEnum.PROPERTIES.getType());
                    ReflectionUtils.invokeMethod(target.getMethod(), target.getBean(), p);
                }else if (type == ConfigTypeEnum.YAML){
                    Properties p = ConfigParseUtils.toProperties(dataVo.getContent(), ConfigTypeEnum.YAML.getType());
                    ReflectionUtils.invokeMethod(target.getMethod(), target.getBean(), p);
                }
            } else {
                log.error("invokeMethod getSourceConfig source is null");
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("invokeMethod can't add Listener for fileName: {} error:{} ",fileName, e);
            }
        }*/
    }

    @Override
    public void onApplicationEvent(MarsListenerEvent managerListenerEvent) {
        MarsListenerSourceTarget target =targetMap.get(managerListenerEvent.getDataConfig().getFileName());
        if(target!=null){
            invokeMethod(target);
        }
    }



    protected boolean isCandidateMethod(Object bean, Class<?> beanClass, MarsListenerEvent listener, Method method) {
        /*if (listener.type()!= ConfigTypeEnum.PROPERTIES || listener.type()!= ConfigTypeEnum.TEXT || listener.type() == ConfigTypeEnum.YAML){
            log.error("ManagerConfigListenerMethodProcessor isCandidateMethod error configType:{}",listener.type());
            return true;
        }
        log.error("ManagerConfigListener  nonsupport "+listener.type().getType()+" isCandidateMethod");*/
        return false;
    }


}
