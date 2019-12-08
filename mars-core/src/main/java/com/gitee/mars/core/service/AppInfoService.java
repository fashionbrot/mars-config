package com.gitee.mars.core.service;

import com.gitee.mars.common.enums.RespCode;
import com.gitee.mars.common.exception.MarsException;
import com.gitee.mars.dao.dao.AppInfoDao;
import com.gitee.mars.dao.entity.AppInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppInfoService {
    @Autowired
    private AppInfoDao appInfoService;


    public void add(AppInfo appInfo) {
        if (appInfoService.add(appInfo)!=1){
            throw new MarsException(RespCode.SAVE_ERROR);
        }
    }


    public void update(AppInfo appInfo) {
        if (appInfoService.update(appInfo)!=1){
            throw new MarsException(RespCode.UPDATE_ERROR);
        }
    }


    public void deleteById(Long id) {
        if(appInfoService.deleteById(id) !=1) {
            throw new MarsException(RespCode.DELETE_ERROR);
        }
    }


    public AppInfo queryById(Long id) {
        return appInfoService.queryById(id);
    }


    public List<AppInfo> queryAll() {
        return appInfoService.queryAll();
    }


    public AppInfo queryByAppName(String appName) {
        return appInfoService.queryByAppName(appName);
    }


    public void deleteByAppName(String appName) {
        if(appInfoService.deleteByAppName(appName) !=1) {
            throw new MarsException(RespCode.DELETE_ERROR);
        }
    }
}
