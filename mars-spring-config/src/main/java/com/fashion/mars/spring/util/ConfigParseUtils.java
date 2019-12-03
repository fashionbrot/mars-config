package com.fashion.mars.spring.util;

import com.fashion.mars.spring.enums.ConfigTypeEnum;
import com.fashion.mars.spring.util.parse.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class ConfigParseUtils {


    private static Map<ConfigTypeEnum, ConfigParse> DEFAULT_CONFIG_PARSE_MAP = new HashMap(8);

    static {
        // register  default ConfigParse
        DEFAULT_CONFIG_PARSE_MAP.put(ConfigTypeEnum.JSON,  new DefaultJsonConfigParse());
        DEFAULT_CONFIG_PARSE_MAP.put(ConfigTypeEnum.PROPERTIES, new DefaultPropertiesConfigParse());
        DEFAULT_CONFIG_PARSE_MAP.put(ConfigTypeEnum.YAML, new DefaultYamlConfigParse());
        DEFAULT_CONFIG_PARSE_MAP.put(ConfigTypeEnum.XML, new DefaultXmlConfigParse());
        DEFAULT_CONFIG_PARSE_MAP = Collections.unmodifiableMap(DEFAULT_CONFIG_PARSE_MAP);
    }

    public static Properties toProperties(final String context, ConfigTypeEnum configTypeEnum) {
        if (StringUtil.isEmpty(context) || configTypeEnum==ConfigTypeEnum.TEXT) {
            return new Properties();
        }
        if (DEFAULT_CONFIG_PARSE_MAP.containsKey(configTypeEnum)) {
            try {
                return DEFAULT_CONFIG_PARSE_MAP.get(configTypeEnum).parse(context);
            }catch (Exception e){
                log.error("parse error:{} context:{} configType:{}",e,context,configTypeEnum.getType());
                return new Properties();
            }
        } else {
            throw new UnsupportedOperationException("Parsing is not yet supported for this configTypeEnum profile : " + configTypeEnum);
        }
    }

}
