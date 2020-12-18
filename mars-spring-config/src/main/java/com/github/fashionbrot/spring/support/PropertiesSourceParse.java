package com.github.fashionbrot.spring.support;

import com.github.fashionbrot.spring.enums.ConfigTypeEnum;
import com.github.fashionbrot.spring.exception.CreatePropertySourceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/10 0:01
 */
@Slf4j
public class PropertiesSourceParse implements SourceParse{


    @Override
    public Properties parse(String context) {
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
    public Properties fileToProperties(File file) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
            log.error("fileToProperties error",e);
        }
        return properties;
    }

    @Override
    public ConfigTypeEnum sourceType() {
        return ConfigTypeEnum.PROPERTIES;
    }


}
