package com.github.fashionbrot.springboot.config;

import com.github.fashionbrot.spring.properties.annotation.MarsConfigurationProperties;
import com.github.fashionbrot.spring.properties.annotation.MarsIgnoreField;
import com.github.fashionbrot.spring.properties.annotation.MarsProperty;
import lombok.Data;
import org.springframework.stereotype.Component;

@MarsConfigurationProperties(fileName = "test",autoRefreshed = true)
@Data
public class TestConfig {

    @MarsProperty("abc")
    public String name ;

//    @MarsProperty("test")
    @MarsIgnoreField
    public String appName ;

    @MarsIgnoreField
    private String sgrTest;

}
