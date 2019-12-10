package com.gitee.mars.spring.event;

import com.gitee.mars.spring.config.MarsDataConfig;
import lombok.Data;
import org.springframework.context.ApplicationEvent;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */
@Data
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
}
