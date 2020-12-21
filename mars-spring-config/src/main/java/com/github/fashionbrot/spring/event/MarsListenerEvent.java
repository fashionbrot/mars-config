package com.github.fashionbrot.spring.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */
public class MarsListenerEvent extends ApplicationEvent {

    private static final long serialVersionUID = 975253233625382817L;

    private String fileName;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public MarsListenerEvent(Object source) {
        super(source);
    }

    public MarsListenerEvent(Object source, String fileName) {
        super(source);
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
