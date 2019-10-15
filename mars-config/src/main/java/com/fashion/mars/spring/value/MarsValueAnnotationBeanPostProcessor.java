package com.fashion.mars.spring.value;

import com.fashion.mars.spring.enums.ConfigTypeEnum;
import com.fashion.mars.spring.event.MarsListenerEvent;
import com.fashion.mars.spring.util.ConfigParseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static com.fashion.mars.spring.util.PropertiesUtil.resolvePlaceholder;
import static org.springframework.core.annotation.AnnotationUtils.getAnnotation;

@Slf4j
public class MarsValueAnnotationBeanPostProcessor extends AnnotationInjectedBeanPostProcessor<MarsValue>
        implements BeanFactoryAware, ApplicationListener<MarsListenerEvent> {

    public static final String BEAN_NAME = "managerValueAnnotationBeanPostProcessor";

    /**
     * placeholder, managerValueTarget
     */
    private Map<String, List<MarsValueTarget>> placeholderValueTargetMap;


    private ConfigurableListableBeanFactory beanFactory;

    public MarsValueAnnotationBeanPostProcessor() {
        placeholderValueTargetMap = new HashMap<>();
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        Assert.isInstanceOf(ConfigurableListableBeanFactory.class, beanFactory, "AnnotationInjectedBeanPostProcessor requires a ConfigurableListableBeanFactory");
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        doWithFields(bean, beanName);

        doWithMethods(bean, beanName);

        return super.postProcessBeforeInitialization(bean, beanName);
    }


    @Override
    protected Object doGetInjectedBean(MarsValue annotation, Object bean, String beanName, Class<?> injectedType, InjectionMetadata.InjectedElement injectedElement) {
        String annotationValue = annotation.value();
        String value = beanFactory.resolveEmbeddedValue(annotationValue);

        Member member = injectedElement.getMember();
        if (member instanceof Field) {
            return convertIfNecessary((Field) member, value);
        }

        if (member instanceof Method) {
            return convertIfNecessary((Method) member, value);
        }

        return null;
    }

    @Override
    protected String buildInjectedObjectCacheKey(MarsValue annotation, Object bean, String beanName, Class<?> injectedType, InjectionMetadata.InjectedElement injectedElement) {
        return bean.getClass().getName() + annotation;
    }


    @Override
    public void onApplicationEvent(MarsListenerEvent event) {
        String content = event.getContent();

        if (!StringUtils.isEmpty(content) && ConfigTypeEnum.YAML == event.getDataConfig().getConfigType() || ConfigTypeEnum.PROPERTIES == event.getDataConfig().getConfigType()) {

            Properties configProperties = ConfigParseUtils.toProperties(event.getDataConfig().getAppId(), event.getDataConfig().getEnvCode(), content, event.getDataConfig().getConfigType().getType());

            for (Object key : configProperties.keySet()) {
                String propertyKey = (String) key;

                List<MarsValueTarget> beanPropertyList = placeholderValueTargetMap.get(propertyKey);
                if (beanPropertyList == null) {
                    continue;
                }

                String propertyValue = configProperties.getProperty(propertyKey);
                for (MarsValueTarget valueTarget : beanPropertyList) {
                    if (valueTarget.method == null) {
                        setField(valueTarget, propertyValue);
                    } else {
                        setMethod(valueTarget, propertyValue);
                    }
                }
            }
        }
    }

    private void setMethod(MarsValueTarget valueTarget, String propertyValue) {
        Method method = valueTarget.method;
        ReflectionUtils.makeAccessible(method);
        try {
            method.invoke(valueTarget.bean, convertIfNecessary(method, propertyValue));

        } catch (Throwable e) {
            if (log.isErrorEnabled()) {
                log.error(
                        "Can't update value with " + method.getName() + " (method) in "
                                + valueTarget.beanName + " (bean)", e);
            }
        }
    }


    private void setField(final MarsValueTarget valueTarget, final String propertyValue) {
        final Object bean = valueTarget.bean;

        Field field = valueTarget.field;

        String fieldName = field.getName();

        try {
            ReflectionUtils.makeAccessible(field);
            field.set(bean, convertIfNecessary(field, propertyValue));

        } catch (Throwable e) {
            if (log.isErrorEnabled()) {
                log.error(
                        "Can't update value of the " + fieldName + " (field) in "
                                + valueTarget.beanName + " (bean)", e);
            }
        }
    }


    private Object convertIfNecessary(Field field, Object value) {
        TypeConverter converter = beanFactory.getTypeConverter();
        return converter.convertIfNecessary(value, field.getType(), field);
    }

    private Object convertIfNecessary(Method method, Object value) {
        Class<?>[] paramTypes = method.getParameterTypes();
        Object[] arguments = new Object[paramTypes.length];

        TypeConverter converter = beanFactory.getTypeConverter();

        if (arguments.length == 1) {
            return converter.convertIfNecessary(value, paramTypes[0], new MethodParameter(method, 0));
        }

        for (int i = 0; i < arguments.length; i++) {
            arguments[i] = converter.convertIfNecessary(value, paramTypes[i], new MethodParameter(method, i));
        }

        return arguments;
    }


    private void doWithFields(final Object bean, final String beanName) {
        ReflectionUtils.doWithFields(bean.getClass(), new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                MarsValue annotation = getAnnotation(field, MarsValue.class);
                doWithAnnotation(beanName, bean, annotation, field.getModifiers(), null, field);
            }
        });
    }

    private void doWithMethods(final Object bean, final String beanName) {
        ReflectionUtils.doWithMethods(bean.getClass(), new ReflectionUtils.MethodCallback() {
            @Override
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                MarsValue annotation = getAnnotation(method, MarsValue.class);
                doWithAnnotation(beanName, bean, annotation, method.getModifiers(), method, null);
            }
        });
    }


    private void doWithAnnotation(String beanName, Object bean, MarsValue annotation, int modifiers, Method method,
                                  Field field) {
        if (annotation != null) {
            if (Modifier.isStatic(modifiers)) {
                return;
            }

            if (annotation.autoRefreshed()) {
                String placeholder = resolvePlaceholder(annotation.value());

                if (placeholder == null) {
                    return;
                }

                MarsValueTarget valueTarget = new MarsValueTarget(bean, beanName, method, field);
                put2ListMap(placeholderValueTargetMap, placeholder, valueTarget);
            }
        }
    }

    private <K, V> void put2ListMap(Map<K, List<V>> map, K key, V value) {
        List<V> valueList = map.get(key);
        if (valueList == null) {
            valueList = new ArrayList<>();
        }
        valueList.add(value);
        map.put(key, valueList);
    }


    private static class MarsValueTarget {

        private Object bean;

        private String beanName;

        private Method method;

        private Field field;

        MarsValueTarget(Object bean, String beanName, Method method, Field field) {
            this.bean = bean;

            this.beanName = beanName;

            this.method = method;

            this.field = field;
        }
    }


}