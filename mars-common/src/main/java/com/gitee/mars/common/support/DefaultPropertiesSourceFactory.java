package com.gitee.mars.common.support;

import com.gitee.mars.common.enums.ConfigTypeEnum;
import com.gitee.mars.common.exception.CreatePropertySourceException;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/10 0:01
 */
public class DefaultPropertiesSourceFactory implements MarsPropertySourceFactory{


    @Override
    public Properties createPropertySource(String context) {
        Properties properties = new Properties();
        try {
            if (StringUtils.hasText(context)) {
                properties.load(new StringReader(context));
            }
        } catch (IOException e) {
            throw new CreatePropertySourceException(e);
        }
        return properties;
    }

    @Override
    public String sourceType() {
        return ConfigTypeEnum.PROPERTIES.getType();
    }


}
