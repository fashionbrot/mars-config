package com.gitee.mars.console.controller;


import com.gitee.mars.common.annotation.IsMenu;
import com.gitee.mars.common.annotation.NoReturnValue;
import com.gitee.mars.common.enums.RespCode;
import com.gitee.mars.common.util.CookieUtil;
import com.gitee.mars.common.vo.PageDataVo;
import com.gitee.mars.common.vo.RespVo;
import com.gitee.mars.core.service.UserInfoService;
import com.gitee.mars.dao.entity.RoleInfo;
import com.gitee.mars.dao.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


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
    public RespVo resetPwd(String pwd,String newPwd){
        return userInfoService.resetPwd(pwd,newPwd);
    }

    @RequestMapping("/add")
    @ResponseBody
    public RespVo add(@RequestBody UserInfo userInfo) {
        userInfoService.add(userInfo);
        return RespVo.success();
    }

    @RequestMapping("/update")
    @ResponseBody
    public RespVo update(@RequestBody UserInfo userInfo) {
        userInfoService.update(userInfo);
        return RespVo.success();
    }

    @RequestMapping("/deleteById")
    @ResponseBody
    public RespVo deleteById(@RequestParam("id") Long id) {
        userInfoService.deleteById(id);
        return RespVo.success();
    }

    @RequestMapping("/queryById")
    @ResponseBody
    public UserInfo queryById(@RequestParam("id") Long id) {
        return userInfoService.queryById(id);
    }

    @RequestMapping("queryAll")
    @ResponseBody
    public PageDataVo<UserInfo> queryAll(@RequestParam(defaultValue = "0") Integer start,
                                         @RequestParam(defaultValue = "20") Integer length) {
        return userInfoService.queryAll(start,length);
    }

    @RequestMapping("queryRoleAll")
    @ResponseBody
    public List<RoleInfo> queryRoleAll(){
        return userInfoService.queryRoleAll();
    }


}
