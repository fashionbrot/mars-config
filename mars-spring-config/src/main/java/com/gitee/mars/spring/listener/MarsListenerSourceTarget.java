package com.gitee.mars.spring.listener;

import com.gitee.mars.spring.listener.annotation.MarsConfigListener;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarsListenerSourceTarget {

    private Object bean;
    private Method method;
    private MarsConfigListener listener;
}
