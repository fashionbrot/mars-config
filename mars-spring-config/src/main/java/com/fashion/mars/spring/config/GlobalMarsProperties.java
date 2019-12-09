package com.fashion.mars.spring.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GlobalMarsProperties {

    public static final String BEAN_NAME = "globalMarsProperties";
    /**
     * application id
     */
    private String appName;
    /**
     * env code
     */
    private String envCode;
    /**
     * server ip:port  separator[,]
     */
    String serverAddress;

    /**
     * listen long poll timeout
     */
    long listenLongPollMs;
    /**
     * listen long poll enabled
     */
    boolean listenLongPollLogEnabled;
}
