package com.github.fashionbrot.console.controller;

import com.github.fashionbrot.common.annotation.IsMenu;
import com.github.fashionbrot.common.annotation.MarsPermission;
import com.github.fashionbrot.common.vo.RespVo;
import com.github.fashionbrot.core.service.MenuService;
import com.github.fashionbrot.dao.entity.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */

@Controller
@RequestMapping("/admin/menu")
public class MenuController {


    @Autowired
    private MenuService envInfoFacade;

    @GetMapping("/index")
    @IsMenu
    public String index(){
        return "/menu/index";
    }

    @RequestMapping(value = "add")
    @ResponseBody
    @MarsPermission("menu:list:add")
    public RespVo add(@RequestBody Menu envInfo) {
        envInfoFacade.add(envInfo);
        return RespVo.success();
    }

    @RequestMapping(value = "update")
    @ResponseBody
    @MarsPermission("menu:list:edit")
    public RespVo update(@RequestBody Menu envInfo) {
        envInfoFacade.update(envInfo);
        return RespVo.success();
    }

    @RequestMapping(value = "deleteById")
    @ResponseBody
    @MarsPermission("menu:list:del")
    public RespVo deleteById(Long id) {
        envInfoFacade.deleteById(id);
        return RespVo.success();
    }

    @RequestMapping(value = "queryById")
    @ResponseBody
    @MarsPermission("menu:list:info")
    public RespVo queryById(Long id) {
        return RespVo.success(envInfoFacade.queryById(id));
    }

    @RequestMapping(value = "queryAllList")
    @ResponseBody
    @MarsPermission("menu:list:list")
    public RespVo queryAllList(Integer page,Integer pageSize) {
        return RespVo.success(envInfoFacade.queryAll(page,pageSize));
    }

    @RequestMapping(value = "queryAll")
    @ResponseBody
    public List<Menu> queryAll() {
        return envInfoFacade.queryAll();
    }

    @RequestMapping(value = "queryMenuLevel")
    @ResponseBody
    public List<Menu> queryMenuLevel(Menu menuBar){
        return  envInfoFacade.queryMenuLevel(menuBar);
    }


    @RequestMapping("loadCheckedMenu")
    @ResponseBody
    public List<Menu> loadCheckedMenu(@RequestParam("roleId") Long roleId){
        return envInfoFacade.loadCheckedMenu(roleId);
    }

    @RequestMapping("loadAllMenu")
    @ResponseBody
    public List<Menu> loadAllMenu(Long roleId){
        return envInfoFacade.loadAllMenu(roleId);
    }

    @RequestMapping("updateRoleMenu")
    @ResponseBody
    @MarsPermission("role:list:update:menu")
    public RespVo updateRoleMenu(Long roleId,String menuIds){
        return envInfoFacade.updateRoleMenu(roleId,menuIds);
    }


}
