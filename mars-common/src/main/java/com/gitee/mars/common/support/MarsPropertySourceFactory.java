package com.gitee.mars.common.support;

import com.gitee.mars.common.exception.CreatePropertySourceException;

import java.util.Properties;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/9 23:55
 */
public interface MarsPropertySourceFactory {

    Properties createPropertySource(String context)throws CreatePropertySourceException;

    String sourceType();
}
