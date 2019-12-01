package com.fashion.mars.spring.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
