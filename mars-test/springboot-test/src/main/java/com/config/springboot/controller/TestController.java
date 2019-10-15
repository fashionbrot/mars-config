package com.config.springboot.controller;

import com.config.springboot.config.TestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Properties;

@Controller
public class TestController {

//    @ManagerValue(value = "${abc:abc}",autoRefreshed = true)
    private String abc;

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

   /* @ManagerConfigListener(fileName = "app.properties")
    public void testP(Properties properties){
        System.out.printf(properties.toString());
    }

    @ManagerConfigListener(fileName = "app.text",type = ConfigTypeEnum.TEXT)
    public void testT(String  properties){
        System.out.printf(properties.toString());
    }


    @ManagerConfigListener(fileName = "app.yaml",type = ConfigTypeEnum.YAML)
    public void testY(Properties  properties){
        System.out.printf(properties.toString());
    }*/

}
