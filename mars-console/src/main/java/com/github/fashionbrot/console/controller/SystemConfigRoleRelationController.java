package com.github.fashionbrot.console.controller;

import com.github.fashionbrot.common.annotation.MarsPermission;
import com.github.fashionbrot.core.service.SystemConfigRoleRelationService;
import com.github.fashionbrot.dao.entity.SystemConfigInfo;
import com.github.fashionbrot.dao.entity.SystemConfigRoleRelation;
import com.github.fashionbrot.common.vo.RespVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */
@RequestMapping("/system-config-role-relation")
@Controller
public class SystemConfigRoleRelationController {

    @Autowired
    private SystemConfigRoleRelationService systemConfigRoleRelationFacade;


    @RequestMapping("/selectBy")
    @ResponseBody
    public List<SystemConfigRoleRelation> selectBy(SystemConfigInfo systemConfigInfo){
        return systemConfigRoleRelationFacade.selectBy(systemConfigInfo);
    }


    @RequestMapping("/sync-role")
    @ResponseBody
    public RespVo syncRole(SystemConfigInfo systemConfigInfo){
        systemConfigRoleRelationFacade.syncRole(systemConfigInfo);
        return RespVo.success();
    }

    @RequestMapping("/save-role")
    @ResponseBody
    @MarsPermission("role:list:update:role")
    public RespVo saveRole(@RequestBody List<SystemConfigRoleRelation> relations){
        systemConfigRoleRelationFacade.saveRole(relations);
        return RespVo.success();
    }

}
