package com.github.fashionbrot.value.event;


import org.springframework.core.Ordered;
import java.util.Map;

public abstract class MarsTemplateKeyMapping implements Ordered {

    /**
     * class 必须实现序列化
     * @return
     */
    public abstract Map<String,Class> initTemplateKeyClass();

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 2;
    }
}

