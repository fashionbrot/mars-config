package com.gitee.mars.console.controller;

import com.gitee.mars.common.annotation.IsMenu;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */

@Controller
public class IndexController {


    @GetMapping("/")
    public String login(){
        return "/login";
    }

    @GetMapping("/index")
    @IsMenu(checkMenuUrlPermission = false)
    public String index(){
        return "index";
    }

    @GetMapping("/control")
    public String control(){
        return "/control";
    }

    @IsMenu(checkMenuUrlPermission = false)
    @RequestMapping("/401")
    public String unauthorized(HttpServletRequest request, String requestUrl){
        request.setAttribute("requestUrl",requestUrl);
        return "/401";
    }

}
