package com.github.fashionbrot.spring.event;

import com.github.fashionbrot.spring.config.MarsDataConfig;
import org.springframework.context.ApplicationEvent;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */
public class MarsListenerEvent extends ApplicationEvent {

    private static final long serialVersionUID = 975253233625382817L;


    private MarsDataConfig dataConfig;

    private String content;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public MarsListenerEvent(Object source) {
        super(source);
    }

    public MarsListenerEvent(Object source, String content, MarsDataConfig dataConfig) {
        super(source);
        this.content = content;
        this.dataConfig = dataConfig;
    }

    public MarsDataConfig getDataConfig() {
        return dataConfig;
    }

    public void setDataConfig(MarsDataConfig dataConfig) {
        this.dataConfig = dataConfig;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
