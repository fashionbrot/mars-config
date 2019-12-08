package com.gitee.mars.console.controller;

import com.gitee.mars.core.service.SystemConfigRoleRelationService;
import com.gitee.mars.dao.entity.SystemConfigInfo;
import com.gitee.mars.dao.entity.SystemConfigRoleRelation;
import com.gitee.mars.common.vo.RespVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

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
    public RespVo saveRole(@RequestBody List<SystemConfigRoleRelation> relations){
        systemConfigRoleRelationFacade.saveRole(relations);
        return RespVo.success();
    }

}
