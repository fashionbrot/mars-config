package com.github.fashionbrot.console.controller;

import com.github.fashionbrot.common.annotation.IsMenu;
import com.github.fashionbrot.common.annotation.MarsPermission;
import com.github.fashionbrot.common.vo.RespVo;
import com.github.fashionbrot.core.service.RoleInfoService;
import com.github.fashionbrot.dao.entity.RoleInfo;
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
@Controller
@RequestMapping("/role")
public class RoleInfoController {
    @Autowired
    private RoleInfoService envInfoFacade;

    @GetMapping("/index")
    @IsMenu
    public String index(){
        return "/role/index";
    }

    @RequestMapping(value = "add")
    @ResponseBody
    @MarsPermission("role:list:add")
    public RespVo add(@RequestBody  RoleInfo roleInfo) {
        envInfoFacade.add(roleInfo);
        return RespVo.success();
    }

    @RequestMapping(value = "update")
    @ResponseBody
    @MarsPermission("role:list:edit")
    public RespVo update(@RequestBody RoleInfo roleInfo) {
        envInfoFacade.update(roleInfo);
        return RespVo.success();
    }

    @RequestMapping(value = "deleteById")
    @ResponseBody
    @MarsPermission("role:list:del")
    public RespVo deleteById(Long id) {
        envInfoFacade.deleteById(id);
        return RespVo.success();
    }

    @RequestMapping(value = "queryById")
    @ResponseBody
    @MarsPermission("role:list:info")
    public RespVo queryById(Long id) {
        return RespVo.success(envInfoFacade.queryById(id));
    }

    @RequestMapping(value = "queryAllList")
    @ResponseBody
    @MarsPermission("role:list:list")
    public RespVo queryAllList() {
        return RespVo.success(envInfoFacade.queryAll());
    }


    @RequestMapping(value = "queryAll")
    @ResponseBody
    public List<RoleInfo> queryAll() {
        return envInfoFacade.queryAll();
    }
}
