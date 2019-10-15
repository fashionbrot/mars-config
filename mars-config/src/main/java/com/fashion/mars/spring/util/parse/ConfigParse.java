package com.fashion.mars.spring.util.parse;

import java.util.Properties;

public interface ConfigParse {

    /**
     * parse config context to map
     *
     * @param configText receive config context
     * @return {@link Properties}
     */
    Properties parse(String configText);

    /**
     * get this ConfigParse process config type
     *
     * @return this parse process type
     */
    String processType();

    /**
     * get config appId
     *
     * @return appId
     */
    String appId();

    /**
     * get config envCode
     *
     * @return envCode
     */
    String envCode();

}