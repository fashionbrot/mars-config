package com.github.fashionbrot.console.controller;


import com.github.fashionbrot.common.annotation.IsMenu;
import com.github.fashionbrot.common.annotation.MarsPermission;
import com.github.fashionbrot.common.annotation.NoReturnValue;
import com.github.fashionbrot.common.util.CookieUtil;
import com.github.fashionbrot.common.vo.PageDataVo;
import com.github.fashionbrot.common.vo.RespVo;
import com.github.fashionbrot.core.service.UserInfoService;
import com.github.fashionbrot.dao.entity.RoleInfo;
import com.github.fashionbrot.dao.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */
@Controller
@RequestMapping("/user")
@Slf4j
public class UserInfoController {


    @Autowired
    private UserInfoService userInfoService;

    @RequestMapping("/login")
    @NoReturnValue
    public String loginHtml(){
        return "/login";
    }


    @RequestMapping("/index")
    @NoReturnValue
    @IsMenu
    public String index(){
        return "/user/userManager";
    }

    @RequestMapping("/authIndex")
    @NoReturnValue
    public String authIndex(){
        return "/userAuthManager";
    }

    @RequestMapping("/doLogin")
    public RespVo login(@RequestParam("userName") String userName,
                        @RequestParam("password") String password, HttpServletRequest request, HttpServletResponse response) {
        return userInfoService.login(userName,password,request,response);
    }

    @RequestMapping("/logout")
    @NoReturnValue
    @IsMenu(checkMenuUrlPermission = false)
    public String logout(HttpServletRequest request,HttpServletResponse response) {
        CookieUtil.deleteCookie(request,response);
        return "/login";
    }

    @RequestMapping("/resetPwd")
    @ResponseBody
    @MarsPermission("user:reset:pwd")
    public RespVo resetPwd(String pwd,String newPwd){
        return userInfoService.resetPwd(pwd,newPwd);
    }

    @RequestMapping("/add")
    @ResponseBody
    @MarsPermission("user:list:add")
    public RespVo add(@RequestBody UserInfo userInfo) {
        userInfoService.add(userInfo);
        return RespVo.success();
    }

    @RequestMapping("/update")
    @ResponseBody
    @MarsPermission("user:list:edit")
    public RespVo update(@RequestBody UserInfo userInfo) {
        userInfoService.update(userInfo);
        return RespVo.success();
    }

    @RequestMapping("/deleteById")
    @ResponseBody
    @MarsPermission("user:list:del")
    public RespVo deleteById(@RequestParam("id") Long id) {
        userInfoService.deleteById(id);
        return RespVo.success();
    }

    @RequestMapping("/queryById")
    @ResponseBody
    @MarsPermission("user:list:info")
    public RespVo queryById(@RequestParam("id") Long id) {
        return RespVo.success(userInfoService.queryById(id));
    }

    @RequestMapping("queryAll")
    @ResponseBody
    @MarsPermission("user:list:list")
    public RespVo queryAll(@RequestParam(defaultValue = "0") Integer start,
                                         @RequestParam(defaultValue = "20") Integer length) {
        return RespVo.success(userInfoService.queryAll(start,length));
    }

    @RequestMapping("queryRoleAll")
    @ResponseBody
    public List<RoleInfo> queryRoleAll(){
        return userInfoService.queryRoleAll();
    }


}
