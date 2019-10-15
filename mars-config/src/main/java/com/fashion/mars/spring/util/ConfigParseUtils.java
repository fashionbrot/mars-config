package com.fashion.mars.spring.util;

import com.fashion.mars.spring.util.parse.ConfigParse;
import com.yooli.appserver.manager.center.config.util.parse.DefaultJsonConfigParse;
import com.yooli.appserver.manager.center.config.util.parse.DefaultPropertiesConfigParse;
import com.yooli.appserver.manager.center.config.util.parse.DefaultXmlConfigParse;
import com.yooli.appserver.manager.center.config.util.parse.DefaultYamlConfigParse;

import java.util.*;

public class ConfigParseUtils {


    private static final String LINK_CHAR = "#@#";
    private static Map<String, ConfigParse> DEFAULT_CONFIG_PARSE_MAP = new HashMap(8);
    private static Map<String, Map<String, ConfigParse>> CUSTOMER_CONFIG_PARSE_MAP = new HashMap(8);

    static {

        DefaultJsonConfigParse jsonConfigParse = new DefaultJsonConfigParse();
        DefaultPropertiesConfigParse propertiesConfigParse = new DefaultPropertiesConfigParse();
        DefaultYamlConfigParse yamlConfigParse = new DefaultYamlConfigParse();
        DefaultXmlConfigParse xmlConfigParse = new DefaultXmlConfigParse();

        // register  default ConfigParse
        DEFAULT_CONFIG_PARSE_MAP.put(jsonConfigParse.processType().toLowerCase(), jsonConfigParse);
        DEFAULT_CONFIG_PARSE_MAP.put(propertiesConfigParse.processType().toLowerCase(), propertiesConfigParse);
        DEFAULT_CONFIG_PARSE_MAP.put(yamlConfigParse.processType().toLowerCase(), yamlConfigParse);
        DEFAULT_CONFIG_PARSE_MAP.put(xmlConfigParse.processType().toLowerCase(), xmlConfigParse);

        // register customer ConfigParse
        ServiceLoader<ConfigParse> configParses = ServiceLoader.load(ConfigParse.class);
        StringBuilder sb = new StringBuilder();
        for (ConfigParse configParse : configParses) {
            String type = configParse.processType().toLowerCase();
            if (!CUSTOMER_CONFIG_PARSE_MAP.containsKey(type)) {
                CUSTOMER_CONFIG_PARSE_MAP.put(type, new HashMap<String, ConfigParse>(1));
            }
            sb.setLength(0);
            sb.append(configParse.appId()).append(LINK_CHAR).append(configParse.envCode());
            if (LINK_CHAR.equals(sb.toString())) {
                // If the user does not set the data id and group processed by config parse,
                // this type of config is resolved globally by default
                DEFAULT_CONFIG_PARSE_MAP.put(type, configParse);
            } else {
                CUSTOMER_CONFIG_PARSE_MAP.get(type).put(sb.toString(), configParse);
            }
        }

        DEFAULT_CONFIG_PARSE_MAP = Collections.unmodifiableMap(DEFAULT_CONFIG_PARSE_MAP);
        CUSTOMER_CONFIG_PARSE_MAP = Collections.unmodifiableMap(CUSTOMER_CONFIG_PARSE_MAP);
    }

    public static Properties toProperties(final String context, String configType) {

        if (context == null) {
            return new Properties();
        }

        configType = configType.toLowerCase();
        Properties properties = new Properties();
        if (DEFAULT_CONFIG_PARSE_MAP.containsKey(configType)) {
            ConfigParse configParse = DEFAULT_CONFIG_PARSE_MAP.get(configType);
            properties.putAll(configParse.parse(context));
            return properties;
        } else {
            throw new UnsupportedOperationException("Parsing is not yet supported for this configType profile : " + configType);
        }
    }

    public static void main(String[] args) {
        String text="test:\n" +
                "\tsgr: 我是test-sgr";
        Properties p=toProperties(text,"yaml");
        System.out.println(p.toString());
    }

    /**
     * XML configuration parsing to support different schemas
     *
     * @param appId   config appId
     * @param envCode config envCode
     * @param context config context
     * @param type    config type
     * @return {@link Properties}
     */
    public static Properties toProperties(final String appId, final String envCode, final String context, final String type) {

        if (context == null) {
            return new Properties();
        }

        StringBuilder sb = new StringBuilder();
        sb.append(appId).append(LINK_CHAR).append(envCode);
        Properties properties = new Properties();
        if (CUSTOMER_CONFIG_PARSE_MAP.isEmpty() || LINK_CHAR.equals(sb.toString())) {
            return toProperties(context, type);
        }
        if (CUSTOMER_CONFIG_PARSE_MAP.get(type) == null || CUSTOMER_CONFIG_PARSE_MAP.get(type).isEmpty()) {
            return toProperties(context, type);
        }
        if (CUSTOMER_CONFIG_PARSE_MAP.get(type).get(sb.toString()) == null) {
            return toProperties(context, type);
        } else {
            if (CUSTOMER_CONFIG_PARSE_MAP.containsKey(type)) {
                ConfigParse configParse = CUSTOMER_CONFIG_PARSE_MAP.get(type).get(sb.toString());
                if (configParse == null) {
                    throw new NoSuchElementException("This config can't find ConfigParse to parse");
                }
                properties.putAll(configParse.parse(context));
                return properties;
            } else {
                throw new UnsupportedOperationException("Parsing is not yet supported for this type profile : " + type);
            }
        }
    }


}
