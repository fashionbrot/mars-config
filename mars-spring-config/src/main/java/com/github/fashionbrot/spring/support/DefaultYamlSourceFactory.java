package com.github.fashionbrot.spring.support;


import com.github.fashionbrot.ribbon.util.CollectionUtil;
import com.github.fashionbrot.spring.enums.ConfigTypeEnum;
import com.github.fashionbrot.spring.exception.CreatePropertySourceException;
import com.github.fashionbrot.spring.util.YamlParser;
import lombok.extern.slf4j.Slf4j;


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
    public String sourceType() {
        return ConfigTypeEnum.YAML.getType();
    }




}
