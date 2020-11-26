package com.github.fashionbrot.value;



import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "mars.value")
public class MarsConfigValueConfigurationProperties {

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
    private Long listenLongPollMs=5000L;

    /**
     * 轮训日志是否开启
     */
    private boolean enableListenLog=false;

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

    public boolean isEnableListenLog() {
        return enableListenLog;
    }

    public void setEnableListenLog(boolean enableListenLog) {
        this.enableListenLog = enableListenLog;
    }
}
