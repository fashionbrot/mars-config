package com.config.springboot.config;

import lombok.Data;

//@MarsConfigurationProperties(fileName = "app.properties",autoRefreshed = true,prefix = "app")
@Data
public class TestConfig {

    public String name ;

//    @ManagerProperty("name")
    public String appName ;

//    @ManagerIgnoreField
    private String sgrTest;

}
