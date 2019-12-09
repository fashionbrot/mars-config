package com.fashion.mars.spring.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */
@Getter
@AllArgsConstructor
public enum ConfigTypeEnum {

    /**
     * config type is "properties"
     */
    PROPERTIES("properties"),

    /**
     * config type is "text"
     */
    TEXT("text"),

    /**
     * config type is "yaml"
     */
    YAML("yaml");

    String type;


    private static Map<String, ConfigTypeEnum> map = new HashMap<String, ConfigTypeEnum>();

    static {
        Arrays.stream(ConfigTypeEnum.values()).forEach(configTypeMenu -> {
            map.put(configTypeMenu.getType(),configTypeMenu);
        });
    }

    public static ConfigTypeEnum valueTypeOf(String type){
        return map.get(type);
    }
}
