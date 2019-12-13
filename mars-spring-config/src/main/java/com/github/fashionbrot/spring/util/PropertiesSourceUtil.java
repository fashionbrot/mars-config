package com.github.fashionbrot.spring.util;

import com.github.fashionbrot.spring.enums.ConfigTypeEnum;
import com.github.fashionbrot.spring.support.DefaultPropertiesSourceFactory;
import com.github.fashionbrot.spring.support.DefaultYamlSourceFactory;
import com.github.fashionbrot.spring.support.MarsPropertySourceFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class PropertiesSourceUtil {


    private static Map<ConfigTypeEnum, MarsPropertySourceFactory> DEFAULT_CONFIG_PARSE_MAP = new HashMap(8);

    static {
        // register  default ConfigParse
        MarsPropertySourceFactory marsPropertySourceFactory = new DefaultPropertiesSourceFactory();
        DEFAULT_CONFIG_PARSE_MAP.put(ConfigTypeEnum.TEXT,  marsPropertySourceFactory);
        DEFAULT_CONFIG_PARSE_MAP.put(ConfigTypeEnum.PROPERTIES, marsPropertySourceFactory);
        DEFAULT_CONFIG_PARSE_MAP.put(ConfigTypeEnum.YAML, new DefaultYamlSourceFactory());
        DEFAULT_CONFIG_PARSE_MAP = Collections.unmodifiableMap(DEFAULT_CONFIG_PARSE_MAP);
    }

    public static Properties toProperties(final String context, ConfigTypeEnum configTypeEnum) {

        if (DEFAULT_CONFIG_PARSE_MAP.containsKey(configTypeEnum)) {
            try {
                return DEFAULT_CONFIG_PARSE_MAP.get(configTypeEnum).createPropertySource(context);
            }catch (Exception e){
                log.error("createPropertySource error:{} context:{} configType:{}",e,context,configTypeEnum.getType());
                return new Properties();
            }
        } else {
            throw new UnsupportedOperationException("Parsing is not yet supported for this configTypeEnum profile : " + configTypeEnum);
        }
    }

}
