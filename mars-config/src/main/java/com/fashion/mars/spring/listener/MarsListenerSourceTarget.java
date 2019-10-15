package com.fashion.mars.spring.listener;

import com.fashion.mars.spring.listener.annotation.MarsConfigListener;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarsListenerSourceTarget {

    private Object bean;
    private Method method;
    private MarsConfigListener listener;
}
