package com.gitee.mars.console.controller;

import com.gitee.mars.common.annotation.IsMenu;
import com.gitee.mars.common.vo.RespVo;
import com.gitee.mars.core.service.EnvInfoService;
import com.gitee.mars.dao.entity.EnvInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/env")
public class EnvInfoController {

    @Autowired
    private EnvInfoService envInfoFacade;

    @GetMapping("/index")
    @IsMenu
    public String index(){
        return "/env/index";
    }

    @RequestMapping(value = "add")
    @ResponseBody
    public RespVo add(@RequestBody EnvInfo envInfo) {
        envInfoFacade.add(envInfo);
        return RespVo.success();
    }

    @RequestMapping(value = "update")
    @ResponseBody
    public RespVo update(@RequestBody EnvInfo envInfo) {
        envInfoFacade.update(envInfo);
        return RespVo.success();
    }

    @RequestMapping(value = "deleteById")
    @ResponseBody
    public RespVo deleteById(Long id) {
        envInfoFacade.deleteById(id);
        return RespVo.success();
    }

    @RequestMapping(value = "queryById")
    @ResponseBody
    public EnvInfo queryById(Long id) {
        return envInfoFacade.queryById(id);
    }

    @RequestMapping(value = "queryAll")
    @ResponseBody
    public List<EnvInfo> queryAll() {
        return envInfoFacade.queryAll();
    }
}
