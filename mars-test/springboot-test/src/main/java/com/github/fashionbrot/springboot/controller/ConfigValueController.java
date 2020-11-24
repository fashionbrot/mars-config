package com.github.fashionbrot.springboot.controller;

import com.github.fashionbrot.value.MarsConfigValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/value")
@Controller
public class ConfigValueController {


    @RequestMapping("get")
    @ResponseBody
    public Object test(String templateKey){

        return MarsConfigValue.getTemplateObject(templateKey);
    }

}
