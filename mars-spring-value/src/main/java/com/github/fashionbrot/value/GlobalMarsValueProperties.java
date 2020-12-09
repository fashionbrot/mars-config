package com.github.fashionbrot.value;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2020/11/23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GlobalMarsValueProperties {

    public static final String BEAN_NAME = "globalMarsValueProperties";
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
    private String serverAddress;

    /**
     * listen long poll timeout
     */
    private long listenLongPollMs;

    private boolean enableListenLog;

    private String localCachePath;

}
