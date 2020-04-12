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

    /**
     * 方式1 获取 abc 配置
     */
    @MarsValue(value = "${abc}",autoRefreshed = true)
    private String abc;

    @RequestMapping("/test")
    @ResponseBody
    public String test(){
        return abc;
    }


    /**
     * 方式2 根据类获取 配置
     */
    @Autowired
    private TestConfig testConfig;


    @RequestMapping("/test2")
    @ResponseBody
    public String test2(){
        return testConfig.appName+":"+testConfig.name;
    }


    /**
     * 方式三根据 配置发生变化获取到监听
     * @param properties
     */
    @MarsConfigListener(fileName = "test",type = ConfigTypeEnum.TEXT,autoRefreshed = false)
    public void testP(String properties){
        System.out.println(properties.toString());
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
