package com.github.fashionbrot.springboot.config;

import com.github.fashionbrot.spring.properties.annotation.MarsConfigurationProperties;
import com.github.fashionbrot.spring.properties.annotation.MarsIgnoreField;
import com.github.fashionbrot.spring.properties.annotation.MarsProperty;
import lombok.Data;
import org.springframework.stereotype.Component;

@MarsConfigurationProperties(fileName = "aaa",autoRefreshed = true)
@Data
@Component
public class TestConfig {

    @MarsProperty("abc")
    public String name ;

    @MarsProperty("abc")
    public String appName ;

    @MarsIgnoreField
    private String sgrTest;

}
