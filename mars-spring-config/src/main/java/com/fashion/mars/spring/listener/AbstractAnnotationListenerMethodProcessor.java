package com.fashion.mars.spring.listener;


import com.fashion.mars.spring.util.MarsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import static java.lang.reflect.Modifier.*;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 *
 */
@Slf4j
public abstract class AbstractAnnotationListenerMethodProcessor<A extends Annotation>
        extends InstantiationAwareBeanPostProcessorAdapter{

    private final Class<A> annotationType;

    public AbstractAnnotationListenerMethodProcessor() {
        this.annotationType = MarsUtil.resolveGenericType(getClass());
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        processBean(beanName,bean);
        return super.postProcessAfterInstantiation(bean, beanName);
    }

    /**
     * Must be
     * <ul>
     * <li><code>public</code></li>
     * <li>not <code>static</code></li>
     * <li>not <code>abstract</code></li>
     * <li>not <code>native</code></li>
     * <li><code>void</code></li>
     * </ul>
     *
     * @param method {@link Method}
     * @return if obey above rules , return <code>true</code>
     */
    static boolean isListenerMethod(Method method) {

        int modifiers = method.getModifiers();

        Class<?> returnType = method.getReturnType();

        return isPublic(modifiers)
                && !isStatic(modifiers)
                && !isNative(modifiers)
                && !isAbstract(modifiers)
                && void.class.equals(returnType)
                ;
    }





    /**
     * Select those methods from bean that annotated
     *
     * @param beanName           Bean name
     * @param bean               Bean object
     */
    private void processBean(final String beanName, final Object bean) {

        ReflectionUtils.doWithMethods(bean.getClass(), new ReflectionUtils.MethodCallback() {
            @Override
            public void doWith(Method method) throws IllegalArgumentException {
                A annotation = AnnotationUtils.getAnnotation(method, annotationType);
                if (annotation != null && isCandidateMethod(bean,bean.getClass(), annotation, method)) {
                    processListenerMethod(beanName, bean, bean.getClass(), annotation, method);
                }
            }

        }, new ReflectionUtils.MethodFilter() {
            @Override
            public boolean matches(Method method) {
                return isListenerMethod(method);
            }
        });

    }

    /**
     * Process Listener Method when {@link #isCandidateMethod(Object, Class, Annotation, Method)} returns <code>true</code>
     *
     * @param beanName           Bean name
     * @param bean               Bean object
     * @param beanClass          Bean Class
     * @param annotation         Annotation object
     * @param method             Method
     */
    protected abstract void processListenerMethod(String beanName, Object bean, Class<?> beanClass, A annotation, Method method);

    /**
     * Subclass could override this method to determine current method is candidate or not
     *
     * @param bean               Bean object
     * @param beanClass          Bean Class
     * @param annotation         Annotation object
     * @param method             Method
     * @return <code>true</code> as default
     */
    protected boolean isCandidateMethod(Object bean, Class<?> beanClass, A annotation, Method method) {
        return true;
    }
}