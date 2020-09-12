package com.github.fashionbrot.console.controller;


import com.github.fashionbrot.common.annotation.IsMenu;
import com.github.fashionbrot.common.annotation.MarsPermission;
import com.github.fashionbrot.common.vo.RespVo;
import com.github.fashionbrot.core.service.AppInfoService;
import com.github.fashionbrot.dao.entity.AppInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 *
 */

@Controller
@RequestMapping("/app")
public class AppInfoController {
    @Autowired
    private AppInfoService appInfoFacade;

    @IsMenu
    @GetMapping("/index")
    public String index(){
        return "/app/index";
    }


    @RequestMapping(value = "add")
    @ResponseBody
    @MarsPermission("app:list:add")
    public RespVo add(@RequestBody AppInfo appInfo) {
        appInfoFacade.add(appInfo);
        return RespVo.success();
    }

    @RequestMapping(value = "update")
    @ResponseBody
    @MarsPermission("app:list:edit")
    public RespVo update(@RequestBody AppInfo appInfo) {
        appInfoFacade.update(appInfo);
        return RespVo.success();
    }

    @RequestMapping(value = "deleteById")
    @ResponseBody
    @MarsPermission("app:list:del")
    public RespVo deleteById(Long id) {
        appInfoFacade.deleteById(id);
        return RespVo.success();
    }

    @RequestMapping(value = "deleteByAppName")
    @ResponseBody
    @MarsPermission("app:list:del")
    public RespVo deleteByAppName(String appName) {
        appInfoFacade.deleteByAppName(appName);
        return RespVo.success();
    }


    @RequestMapping(value = "/queryById")
    @ResponseBody
    @MarsPermission("app:list:info")
    public RespVo queryById(String appName) {
        return RespVo.success(appInfoFacade.queryByAppName(appName));
    }

    @RequestMapping(value = "queryAllList")
    @ResponseBody
    @MarsPermission("app:list:list")
    public RespVo queryAllList() {
        return RespVo.success(appInfoFacade.queryAll());
    }

    @RequestMapping(value = "queryAll")
    @ResponseBody
    public List<AppInfo> queryAll() {
        return appInfoFacade.queryAll();
    }


}
