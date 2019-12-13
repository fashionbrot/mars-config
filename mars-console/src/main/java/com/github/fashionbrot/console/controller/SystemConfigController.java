package com.github.fashionbrot.console.controller;

import com.github.fashionbrot.common.annotation.IsMenu;
import com.github.fashionbrot.common.vo.PageDataVo;
import com.github.fashionbrot.common.vo.RespVo;
import com.github.fashionbrot.core.service.SystemConfigService;
import com.github.fashionbrot.dao.entity.SystemConfigHistoryInfo;
import com.github.fashionbrot.dao.entity.SystemConfigInfo;
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
 */
@RequestMapping("/system")
@Controller
public class SystemConfigController  {
    @Autowired
    private SystemConfigService systemConfigFacade;

    @GetMapping("/index")
    @IsMenu
    public String index(){
        return "/system/index";
    }

    @GetMapping("/history-index")
    @IsMenu
    public String historyIndex(){
        return "/systemHistory/index";
    }

    @RequestMapping(value = "add")
    @ResponseBody
    public RespVo add(@RequestBody SystemConfigInfo systemConfigInfo) {
        systemConfigFacade.add(systemConfigInfo);
        return RespVo.success();
    }

    @RequestMapping(value = "update")
    @ResponseBody
    public RespVo update(@RequestBody SystemConfigInfo systemConfigInfo) {
        systemConfigFacade.update(systemConfigInfo);
        return RespVo.success();
    }

    @RequestMapping(value = "deleteById")
    @ResponseBody
    public RespVo deleteById(Long id) {
        systemConfigFacade.deleteById(id);
        return RespVo.success();
    }

    @RequestMapping(value = "queryById")
    @ResponseBody
    public RespVo queryById(Long id) {
        return RespVo.success(systemConfigFacade.queryById(id));
    }

    @RequestMapping(value = "queryAll")
    @ResponseBody
    public List<SystemConfigInfo> queryAll() {
        return systemConfigFacade.queryAll();
    }

    @RequestMapping(value = "queryByAppAndEnv")
    @ResponseBody
    public List<SystemConfigInfo> queryByAppAndEnv(String appName, String envCode) {
        return systemConfigFacade.queryByAppAndEnv(appName, envCode);
    }




    @RequestMapping(value = "publishById")
    @ResponseBody
    public RespVo publishByFileId(Long id){
        systemConfigFacade.publish(id);
        return RespVo.success();
    }


    @RequestMapping(value = "queryHistoryAll")
    @ResponseBody
    public PageDataVo<SystemConfigHistoryInfo> queryHistoryAll(SystemConfigHistoryInfo info, Integer start, Integer length){
        return systemConfigFacade.queryHistoryAll(info,start,length);
    }

    @RequestMapping(value = "queryHistoryById")
    public SystemConfigHistoryInfo  queryHistoryById(Long id){
        return systemConfigFacade.queryHistoryById(id);
    }


    @RequestMapping(value = "deleteHistoryById")
    @ResponseBody
    public RespVo deleteHistoryById(Long id) {
        systemConfigFacade.deleteHistoryById(id);
        return RespVo.success();
    }

    @RequestMapping(value = "rollBackById")
    @ResponseBody
    public RespVo rollBackById(Long id){
        systemConfigFacade.rollBackById(id);
        return RespVo.success();
    }

}
