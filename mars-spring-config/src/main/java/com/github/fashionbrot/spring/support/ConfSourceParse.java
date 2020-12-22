package com.github.fashionbrot.spring.support;

import com.github.fashionbrot.ribbon.util.CollectionUtil;
import com.github.fashionbrot.spring.enums.ConfigTypeEnum;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@Slf4j
public class ConfSourceParse implements SourceParse {
    @Override
    public Properties parse(String context) {
        Properties p = null;
        try {
            p = parseProperties(context);
        }catch (Exception e){
            log.error("ConfSourceParse parse error context:{}",context,e);
        }
        if (p==null){
            p = new Properties();
        }
        return p;
    }

    @Override
    public Properties fileToProperties(File file) {
        Config  config = null;
        try {
            config = ConfigFactory.load(file.getPath());
        }catch (Exception e){
            log.error("ConfSourceParse fileToProperties error",e);
        }
        Properties p = parseProperties(config);
        if (p==null){
            p = new Properties();
        }
        return p;
    }

    @Override
    public ConfigTypeEnum sourceType() {
        return ConfigTypeEnum.CONF;
    }

    public static void main(String[] args) {

        Config  config = ConfigFactory.parseString("lobbyserver= {\n" +
                "  ip= ${globalIP}\n" +
                "  port= 9900\n" +
                "  rpc= 9901\n" +
                "}\n");
        System.out.println(config.getString("lobbyserver.port"));

        String test ="{test{t1={t1=2}\n t2=2}}";
        Properties p = parseProperties(test);
        System.out.println(p.entrySet());
    }

    private static Properties parseProperties(String text){
        Config  config = ConfigFactory.parseString(text);
        return parseProperties(config);
    }

    private static Properties parseProperties(Config  config){
        Properties properties = new Properties();
        if (config!=null){
            Set<Map.Entry<String, ConfigValue>> entries = config.entrySet();
            if (CollectionUtil.isNotEmpty(entries)){
                for(Map.Entry<String, ConfigValue> set : entries){
                    properties.put(set.getKey(),set.getValue().render());
                }
            }
        }
        return properties;
    }
}
