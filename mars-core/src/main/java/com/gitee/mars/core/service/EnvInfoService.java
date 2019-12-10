package com.gitee.mars.core.service;


import com.gitee.mars.common.enums.RespCode;
import com.gitee.mars.common.exception.MarsException;
import com.gitee.mars.dao.dao.EnvInfoDao;
import com.gitee.mars.dao.entity.EnvInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */
@Service
public class EnvInfoService {

    @Autowired
    private EnvInfoDao envInfoDao;


    public void add(EnvInfo appInfo) {
        if (envInfoDao.add(appInfo)!=1){
            throw new MarsException(RespCode.SAVE_ERROR);
        }
    }


    public void update(EnvInfo appInfo) {
        if (envInfoDao.update(appInfo)!=1){
            throw new MarsException(RespCode.UPDATE_ERROR);
        }
    }


    public void deleteById(Long id) {
        if(envInfoDao.deleteById(id) !=1){
            throw new MarsException(RespCode.DELETE_ERROR);
        }
    }


    public EnvInfo queryById(Long id) {
        return envInfoDao.queryById(id);
    }


    public List<EnvInfo> queryAll() {
        return envInfoDao.queryAll(null);
    }
}
