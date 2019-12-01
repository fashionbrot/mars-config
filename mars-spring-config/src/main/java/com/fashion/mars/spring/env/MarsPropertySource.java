package com.fashion.mars.spring.env;

import com.fashion.mars.spring.config.MarsDataConfig;
import lombok.Builder;
import lombok.Data;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.Properties;

@Data
public class MarsPropertySource extends PropertiesPropertySource {

    private MarsDataConfig marsDataConfig;

    public MarsPropertySource( String name, Properties source ,MarsDataConfig marsDataConfig) {
        super(name, source);
        this.marsDataConfig =  marsDataConfig;
    }

}
