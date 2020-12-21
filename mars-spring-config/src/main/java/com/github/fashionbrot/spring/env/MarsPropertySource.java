package com.github.fashionbrot.spring.env;

import org.springframework.core.env.PropertiesPropertySource;

import java.util.Properties;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */
public class MarsPropertySource extends PropertiesPropertySource {


    public MarsPropertySource( String name, Properties source ) {
        super(name, source);
    }
}
