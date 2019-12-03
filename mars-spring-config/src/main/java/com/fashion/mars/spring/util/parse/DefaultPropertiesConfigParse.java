
package com.fashion.mars.spring.util.parse;

import com.fashion.mars.spring.enums.ConfigTypeEnum;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;


public class DefaultPropertiesConfigParse implements ConfigParse {


    @Override
    public Properties parse(String configText) {
        Properties properties = new Properties();
        try {
            if (StringUtils.hasText(configText)) {
                properties.load(new StringReader(configText));
            }
        } catch (IOException e) {
            throw new ConfigParseException(e);
        }
        return properties;
    }

    @Override
    public ConfigTypeEnum configType() {
        return ConfigTypeEnum.PROPERTIES;
    }

}

