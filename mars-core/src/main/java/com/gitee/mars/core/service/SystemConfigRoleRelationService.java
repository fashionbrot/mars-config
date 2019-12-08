package com.gitee.mars.core.service;

import com.gitee.mars.common.enums.RespCode;
import com.gitee.mars.common.exception.MarsException;
import com.gitee.mars.dao.dao.SystemConfigRoleRelationDao;
import com.gitee.mars.dao.entity.SystemConfigInfo;
import com.gitee.mars.dao.entity.SystemConfigRoleRelation;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SystemConfigRoleRelationService {

    @Autowired
    private SystemConfigRoleRelationDao systemConfigRoleRelationService;



    public void add(SystemConfigRoleRelation systemConfigRoleRelation) {
         if(systemConfigRoleRelationService.add(systemConfigRoleRelation)!=1){
             throw new MarsException(RespCode.SAVE_ERROR);
         }
    }


    public void update(SystemConfigRoleRelation appInfo) {
        if (systemConfigRoleRelationService.update(appInfo)!=1){
            throw new MarsException(RespCode.UPDATE_ERROR);
        }
    }


    public void deleteById(Long id) {
        if (systemConfigRoleRelationService.deleteById(id)!=1){
            throw new MarsException(RespCode.DELETE_ERROR);
        }
    }


    public SystemConfigRoleRelation queryById(Long id) {
        return systemConfigRoleRelationService.queryById(id);
    }


    public List<SystemConfigRoleRelation> queryAll() {
        List<SystemConfigRoleRelation> menuBarList= systemConfigRoleRelationService.queryAll();

        return menuBarList;
    }



    public List<SystemConfigRoleRelation> selectBy(SystemConfigInfo systemConfigInfo) {

        return systemConfigRoleRelationService.selectBy(systemConfigInfo);
    }


    public int syncRole(SystemConfigInfo systemConfigInfo) {
        return systemConfigRoleRelationService.syncRole(systemConfigInfo);
    }


    public void saveRole(List<SystemConfigRoleRelation> relations) {
        if (systemConfigRoleRelationService.saveRole(relations)<=0){
            throw new MarsException(RespCode.SAVE_ERROR);
        }
    }
}
