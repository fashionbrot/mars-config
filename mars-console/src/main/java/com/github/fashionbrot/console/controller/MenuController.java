package com.github.fashionbrot.console.controller;

import com.github.fashionbrot.common.annotation.IsMenu;
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
    public RespVo add(@RequestBody Menu envInfo) {
        envInfoFacade.add(envInfo);
        return RespVo.success();
    }

    @RequestMapping(value = "update")
    @ResponseBody
    public RespVo update(@RequestBody Menu envInfo) {
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
    public Menu queryById(Long id) {
        return envInfoFacade.queryById(id);
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
    public RespVo updateRoleMenu(Long roleId,String menuIds){
        return envInfoFacade.updateRoleMenu(roleId,menuIds);
    }


}
