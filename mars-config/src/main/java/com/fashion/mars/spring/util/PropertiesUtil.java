package com.fashion.mars.spring.util;


import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Properties;

public class PropertiesUtil {


    private static final String PLACEHOLDER_PREFIX = "${";

    private static final String PLACEHOLDER_SUFFIX = "}";

    private static final String VALUE_SEPARATOR = ":";


    public static String resolvePlaceholder(String placeholder) {
        if (!placeholder.startsWith(PLACEHOLDER_PREFIX)) {
            return null;
        }

        if (!placeholder.endsWith(PLACEHOLDER_SUFFIX)) {
            return null;
        }

        if (placeholder.length() <= PLACEHOLDER_PREFIX.length() + PLACEHOLDER_SUFFIX.length()) {
            return null;
        }

        int beginIndex = PLACEHOLDER_PREFIX.length();
        int endIndex = placeholder.length() - PLACEHOLDER_PREFIX.length() + 1;
        placeholder = placeholder.substring(beginIndex, endIndex);

        int separatorIndex = placeholder.indexOf(VALUE_SEPARATOR);
        if (separatorIndex != -1) {
            return placeholder.substring(0, separatorIndex);
        }

        return placeholder;
    }



    public static Properties resolve(Map<?, ?> properties,Properties source) {
        Properties resolvedProperties = new Properties();
        for (Map.Entry<?, ?> entry : properties.entrySet()) {
            if (entry.getValue() instanceof CharSequence) {
                String key = String.valueOf(entry.getKey());
                String value = String.valueOf(entry.getValue());

                String resolvedKey = resolvePlaceholder(value);
                if (StringUtils.isEmpty(resolvedKey)){
                    resolvedProperties.setProperty(key, value);
                }else{
                    if (source!=null && source.containsKey(resolvedKey)){
                        String resolvedValue = source.getProperty( resolvedKey) ;
                        resolvedProperties.setProperty(key, resolvedValue);
                    }else{
                        resolvedProperties.setProperty(key, "");
                    }
                }
            }
        }
        return resolvedProperties;
    }


}
