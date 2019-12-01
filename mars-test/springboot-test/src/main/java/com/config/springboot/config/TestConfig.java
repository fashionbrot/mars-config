package com.config.springboot.config;

import com.fashion.mars.spring.properties.annotation.MarsConfigurationProperties;
import com.fashion.mars.spring.properties.annotation.MarsIgnoreField;
import com.fashion.mars.spring.properties.annotation.MarsProperty;
import lombok.Data;
import org.springframework.stereotype.Component;

@MarsConfigurationProperties(fileName = "aaa.properties",autoRefreshed = true)
@Data
@Component
public class TestConfig {

    @MarsProperty("abc")
    public String name ;

//    @MarsProperty("name")
    public String appName ;

    @MarsIgnoreField
    private String sgrTest;

}
