
package com.fashion.mars.spring.util.parse;


import com.fashion.mars.spring.enums.ConfigTypeEnum;

import java.util.Map;
import java.util.Properties;

import static com.fashion.mars.spring.util.parse.DefaultYamlConfigParse.createYaml;


public class DefaultJsonConfigParse implements ConfigParse {

    @Override
    public Properties parse(String configText) {
        final Properties result = new Properties();
        DefaultYamlConfigParse.process(new DefaultYamlConfigParse.MatchCallback() {
            @Override
            public void process(Properties properties, Map<String, Object> map) {
                result.putAll(properties);
            }
        }, createYaml(), configText);
        return result;
    }

    @Override
    public ConfigTypeEnum configType() {
        return ConfigTypeEnum.JSON;
    }

}

