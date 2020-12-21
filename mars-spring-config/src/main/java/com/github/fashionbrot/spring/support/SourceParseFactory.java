package com.github.fashionbrot.spring.support;

import com.github.fashionbrot.spring.enums.ConfigTypeEnum;

import java.io.File;
import java.util.Map;
import java.util.Properties;
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

    public static Properties toProperties(final String context, final ConfigTypeEnum configTypeEnum) {
        return SourceParseFactory.getSourceParse(configTypeEnum).parse(context);
    }

    public static Properties toProperties(final File file, final ConfigTypeEnum configTypeEnum) {
        return SourceParseFactory.getSourceParse(configTypeEnum).fileToProperties(file);
    }

    public static void main(String[] args) {
        String text ="{test{t1={abc=121} \n t2=cdd}}";
        Properties p = toProperties(text,ConfigTypeEnum.CONF);
        System.out.println(p.entrySet());
    }

}
