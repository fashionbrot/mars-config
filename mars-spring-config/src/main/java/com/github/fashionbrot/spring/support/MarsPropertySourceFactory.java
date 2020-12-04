package com.github.fashionbrot.spring.support;

import com.github.fashionbrot.spring.exception.CreatePropertySourceException;

import java.io.File;
import java.util.Properties;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/9 23:55
 */
public interface MarsPropertySourceFactory {

    Properties createPropertySource(String context)throws CreatePropertySourceException;

    Properties fileToProperties(File file);

    String sourceType();
}
