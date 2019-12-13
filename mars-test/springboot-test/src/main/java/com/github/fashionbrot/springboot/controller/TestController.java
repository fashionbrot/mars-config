package com.github.fashionbrot.springboot.controller;

import com.github.fashionbrot.springboot.config.TestConfig;
import com.github.fashionbrot.spring.enums.ConfigTypeEnum;
import com.github.fashionbrot.spring.listener.annotation.MarsConfigListener;
import com.github.fashionbrot.spring.value.MarsValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Properties;

@Controller
public class TestController {

    @MarsValue(value = "${abc}",autoRefreshed = true)
    private String abc;

    private Properties test;

    @RequestMapping("/test")
    @ResponseBody
    public String test(){
        return abc;
    }

    @Autowired
    private TestConfig testConfig;

    @RequestMapping("/test2")
    @ResponseBody
    public String test2(){
        return testConfig.appName+":"+testConfig.name;
    }

    @MarsConfigListener(fileName = "aaa",type = ConfigTypeEnum.TEXT)
    public void testP(String properties){
        System.out.printf(properties.toString());
    }
    /**
    @MarsConfigListener(fileName = "app.text",type = ConfigTypeEnum.TEXT)
    public void testT(String  properties){
        System.out.printf(properties.toString());
    }


    @MarsConfigListener(fileName = "app.yaml",type = ConfigTypeEnum.YAML)
    public void testY(Properties  properties){
        System.out.printf(properties.toString());
    }*/

}
