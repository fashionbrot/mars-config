package com.github.fashionbrot.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "mars.config")
public class MarsConfigConfigurationProperties {

    /**
     * 环境code
     */
    private String envCode;

    /**
     * 应用id
     */
    private String appId;

    /**
     * 服务器地址 多个逗号分隔
     */
    private String serverAddress;

    /**
     * 客户端轮训毫秒数
     */
    private Long listenLongPollMs=30000L;


    /**
     * Whether to enable local caching
     * @return
     */
    private String enableLocalCache;

    /**
     * Local cache file path
     * @return
     */
    private String localCachePath;

    public String getEnvCode() {
        return envCode;
    }

    public void setEnvCode(String envCode) {
        this.envCode = envCode;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public Long getListenLongPollMs() {
        return listenLongPollMs;
    }

    public void setListenLongPollMs(Long listenLongPollMs) {
        this.listenLongPollMs = listenLongPollMs;
    }

    public String getEnableLocalCache() {
        return enableLocalCache;
    }

    public void setEnableLocalCache(String enableLocalCache) {
        this.enableLocalCache = enableLocalCache;
    }

    public String getLocalCachePath() {
        return localCachePath;
    }

    public void setLocalCachePath(String localCachePath) {
        this.localCachePath = localCachePath;
    }
}
