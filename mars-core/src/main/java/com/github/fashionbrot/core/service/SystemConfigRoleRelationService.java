package com.github.fashionbrot.core.service;

import com.github.fashionbrot.common.enums.RespCode;
import com.github.fashionbrot.common.exception.MarsException;
import com.github.fashionbrot.dao.dao.SystemConfigRoleRelationDao;
import com.github.fashionbrot.dao.entity.SystemConfigInfo;
import com.github.fashionbrot.dao.entity.SystemConfigRoleRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */
@Service
public class SystemConfigRoleRelationService {

    @Autowired
    private SystemConfigRoleRelationDao systemConfigRoleRelationDao;



    public void add(SystemConfigRoleRelation systemConfigRoleRelation) {
         if(systemConfigRoleRelationDao.add(systemConfigRoleRelation)!=1){
             throw new MarsException(RespCode.SAVE_ERROR);
         }
    }


    public void update(SystemConfigRoleRelation appInfo) {
        if (systemConfigRoleRelationDao.update(appInfo)!=1){
            throw new MarsException(RespCode.UPDATE_ERROR);
        }
    }


    public void deleteById(Long id) {
        if (systemConfigRoleRelationDao.deleteById(id)!=1){
            throw new MarsException(RespCode.DELETE_ERROR);
        }
    }


    public SystemConfigRoleRelation queryById(Long id) {
        return systemConfigRoleRelationDao.queryById(id);
    }


    public List<SystemConfigRoleRelation> queryAll() {
        List<SystemConfigRoleRelation> menuBarList= systemConfigRoleRelationDao.queryAll();

        return menuBarList;
    }



    public List<SystemConfigRoleRelation> selectBy(SystemConfigInfo systemConfigInfo) {

        return systemConfigRoleRelationDao.selectBy(systemConfigInfo);
    }


    public int syncRole(SystemConfigInfo systemConfigInfo) {
        return systemConfigRoleRelationDao.syncRole(systemConfigInfo);
    }


    public void saveRole(List<SystemConfigRoleRelation> relations) {
        if (systemConfigRoleRelationDao.saveRole(relations)<=0){
            throw new MarsException(RespCode.SAVE_ERROR);
        }
    }
}
