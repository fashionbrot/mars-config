package com.gitee.mars.spring.util;

import com.gitee.mars.spring.properties.annotation.MarsIgnoreField;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

public  class ObjectUtils {

    public static void cleanMapOrCollectionField(final Object bean) {
        ReflectionUtils.doWithFields(bean.getClass(), new ReflectionUtils.FieldCallback() {

            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                field.setAccessible(true);
                if (field.isAnnotationPresent(MarsIgnoreField.class)) {
                    return;
                }
                Class<?> type = field.getType();
                if (type.isAssignableFrom(Map.class) || Collection.class.isAssignableFrom(type)) {
                    field.set(bean, null);
                }
            }
        });
    }

}