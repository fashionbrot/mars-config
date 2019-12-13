package com.github.fashionbrot.spring.util;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.PropertyResolver;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Properties;

/**
 * Placeholder Resolver for {@link Properties properties}
 */
public class PropertiesPlaceholderResolver {

    private final PropertyResolver propertyResolver;

    public PropertiesPlaceholderResolver(PropertyResolver propertyResolver) {
        this.propertyResolver = propertyResolver;
    }

    /**
     * Resolve placeholders in specified {@link Annotation annotation}
     *
     * @param annotation {@link Annotation annotation}
     * @return Resolved {@link Properties source properties}
     */
    public Properties resolve(Annotation annotation) {
        Map<String, Object> attributes = AnnotationUtils.getAnnotationAttributes(annotation);
        return resolve(attributes);
    }

    /**
     * Resolve placeholders in specified {@link Map properties}
     *
     * @param properties {@link Map source properties}
     * @return Resolved {@link Properties source properties}
     */
    public Properties resolve(Map<?, ?> properties) {
        Properties resolvedProperties = new Properties();
        for (Map.Entry<?, ?> entry : properties.entrySet()) {
            if (entry.getValue() instanceof CharSequence) {
                String key = String.valueOf(entry.getKey());
                String value = String.valueOf(entry.getValue());
                if (!StringUtils.isEmpty(value)) {
                    String resolvedValue = propertyResolver.resolvePlaceholders(value);// set properties if has test
                    resolvedProperties.setProperty(key, resolvedValue);
                }else{
                    resolvedProperties.setProperty(key, value);
                }
            }
        }
        return resolvedProperties;
    }

    public Properties resolveNull(Map<?, ?> properties) {
        Properties resolvedProperties = new Properties();
        for (Map.Entry<?, ?> entry : properties.entrySet()) {
            if (entry.getValue() instanceof CharSequence) {
                String key = String.valueOf(entry.getKey());
                String value = String.valueOf(entry.getValue());
                String resolvedValue = propertyResolver.resolvePlaceholders(value);

                if (value.startsWith("${") && value.endsWith("}")) {
                    if (!value.equals(resolvedValue) && StringUtils.hasText(resolvedValue)) {
                        resolvedProperties.setProperty(key, resolvedValue);
                    } else {
                        resolvedProperties.setProperty(key, "");
                    }
                } else {
                    resolvedProperties.setProperty(key, value);
                }
            }
        }
        return resolvedProperties;
    }


}