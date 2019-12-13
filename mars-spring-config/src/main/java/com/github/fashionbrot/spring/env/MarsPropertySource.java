package com.github.fashionbrot.spring.env;

import com.github.fashionbrot.spring.config.MarsDataConfig;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.Properties;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */
public class MarsPropertySource extends PropertiesPropertySource {

    private MarsDataConfig marsDataConfig;

    public MarsPropertySource( String name, Properties source ,MarsDataConfig marsDataConfig) {
        super(name, source);
        this.marsDataConfig =  marsDataConfig;
    }


    public MarsDataConfig getMarsDataConfig() {
        return marsDataConfig;
    }

    public void setMarsDataConfig(MarsDataConfig marsDataConfig) {
        this.marsDataConfig = marsDataConfig;
    }
}
