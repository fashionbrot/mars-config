package com.gitee.mars.core.service;


import com.gitee.mars.common.enums.RespCode;
import com.gitee.mars.common.exception.MarsException;
import com.gitee.mars.dao.dao.EnvInfoDao;
import com.gitee.mars.dao.entity.EnvInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class EnvInfoService {
    @Autowired
    private EnvInfoDao envInfoService;


    public void add(EnvInfo appInfo) {
        if (envInfoService.add(appInfo)!=1){
            throw new MarsException(RespCode.SAVE_ERROR);
        }
    }


    public void update(EnvInfo appInfo) {
        if (envInfoService.update(appInfo)!=1){
            throw new MarsException(RespCode.UPDATE_ERROR);
        }
    }


    public void deleteById(Long id) {
        if(envInfoService.deleteById(id) !=1){
            throw new MarsException(RespCode.DELETE_ERROR);
        }
    }


    public EnvInfo queryById(Long id) {
        return envInfoService.queryById(id);
    }


    public List<EnvInfo> queryAll() {
        return envInfoService.queryAll(null);
    }
}
