package com.github.fashionbrot.spring.support;


import com.github.fashionbrot.ribbon.util.CollectionUtil;
import com.github.fashionbrot.spring.enums.ConfigTypeEnum;
import com.github.fashionbrot.spring.exception.CreatePropertySourceException;
import com.github.fashionbrot.spring.util.YamlParser;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/10 0:06
 */

@Slf4j
public class DefaultYamlSourceFactory implements MarsPropertySourceFactory {


    @Override
    public Properties createPropertySource( String context){
        Properties result = new Properties();
        Map<String, Object> map = YamlParser.yamlToFlattenedMap(context);
        if (CollectionUtil.isNotEmpty(map)){
            result.putAll(map);
        }
        return result;
    }

    @Override
    public Properties fileToProperties(File file) {
        Properties properties=new Properties();
        Yaml yaml =new Yaml();
        try {
            Map<String,Object> map = yaml.load(new FileInputStream(file));
            if (CollectionUtil.isNotEmpty(map)) {
                properties.putAll(map);
            }
        } catch (FileNotFoundException e) {
            log.error("fileToProperties error",e);
        }
        return properties;
    }

    @Override
    public String sourceType() {
        return ConfigTypeEnum.YAML.getType();
    }




}
