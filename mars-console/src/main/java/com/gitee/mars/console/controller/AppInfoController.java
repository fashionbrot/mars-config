package com.gitee.mars.console.controller;


import com.gitee.mars.common.annotation.IsMenu;
import com.gitee.mars.common.vo.RespVo;
import com.gitee.mars.core.service.AppInfoService;
import com.gitee.mars.dao.entity.AppInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 项目管理 controller
 */

@Controller
@RequestMapping("/app")
public class AppInfoController {
    @Autowired
    private AppInfoService appInfoFacade;
    /**
     * 项目列表
     * @return
     */
    @IsMenu
    @GetMapping("/index")
    public String index(){
        return "/app/index";
    }


    @RequestMapping(value = "add")
    @ResponseBody
    public RespVo add(@RequestBody AppInfo appInfo) {
        appInfoFacade.add(appInfo);
        return RespVo.success();
    }

    @RequestMapping(value = "update")
    @ResponseBody
    public RespVo update(@RequestBody AppInfo appInfo) {
        appInfoFacade.update(appInfo);
        return RespVo.success();
    }

    @RequestMapping(value = "deleteById")
    @ResponseBody
    public RespVo deleteById(Long id) {
        appInfoFacade.deleteById(id);
        return RespVo.success();
    }

    @RequestMapping(value = "deleteByAppName")
    @ResponseBody
    public RespVo deleteByAppName(String appName) {
        appInfoFacade.deleteByAppName(appName);
        return RespVo.success();
    }


    @RequestMapping(value = "/queryById")
    @ResponseBody
    public AppInfo queryById(String appName) {
        return appInfoFacade.queryByAppName(appName);
    }

    @RequestMapping(value = "queryAll")
    @ResponseBody
    public List<AppInfo> queryAll() {
        return appInfoFacade.queryAll();
    }


}
