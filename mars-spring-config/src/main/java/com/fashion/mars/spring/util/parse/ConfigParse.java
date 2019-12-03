package com.fashion.mars.spring.util.parse;

import com.fashion.mars.spring.enums.ConfigTypeEnum;

import java.util.Properties;

public interface ConfigParse {

    /**
     * parse config context to Properties
     * @return {@link Properties}
     */
    Properties parse(String context);

    /**
     * config type
     * @return {@link ConfigTypeEnum}
     */
    ConfigTypeEnum configType();
}