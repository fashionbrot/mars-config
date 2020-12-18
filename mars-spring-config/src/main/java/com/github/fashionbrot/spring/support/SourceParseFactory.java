package com.github.fashionbrot.spring.support;

import com.github.fashionbrot.spring.enums.ConfigTypeEnum;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public class SourceParseFactory {

    private static Map<ConfigTypeEnum,SourceParse> parseMap = new ConcurrentHashMap<>();

    static {
        SourceParse propertiesSourceParse = new PropertiesSourceParse();
        parseMap.put(ConfigTypeEnum.NONE,propertiesSourceParse);

        ServiceLoader<SourceParse> serviceLoader = ServiceLoader.load(SourceParse.class);
        for(SourceParse service : serviceLoader) {
            parseMap.put(service.sourceType(),service);
        }
    }

    public static SourceParse getSourceParse(ConfigTypeEnum configType){
        if (parseMap.containsKey(configType)){
            return parseMap.get(configType);
        }
        return parseMap.get(ConfigTypeEnum.NONE);
    }

}
