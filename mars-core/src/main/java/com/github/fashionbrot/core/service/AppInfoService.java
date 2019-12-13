package com.github.fashionbrot.core.service;

import com.github.fashionbrot.common.enums.RespCode;
import com.github.fashionbrot.common.exception.MarsException;
import com.github.fashionbrot.dao.dao.AppInfoDao;
import com.github.fashionbrot.dao.entity.AppInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */
@Service
public class AppInfoService {
    @Autowired
    private AppInfoDao appInfoDao;


    public void add(AppInfo appInfo) {
        if (appInfoDao.add(appInfo)!=1){
            throw new MarsException(RespCode.SAVE_ERROR);
        }
    }


    public void update(AppInfo appInfo) {
        if (appInfoDao.update(appInfo)!=1){
            throw new MarsException(RespCode.UPDATE_ERROR);
        }
    }


    public void deleteById(Long id) {
        if(appInfoDao.deleteById(id) !=1) {
            throw new MarsException(RespCode.DELETE_ERROR);
        }
    }


    public AppInfo queryById(Long id) {
        return appInfoDao.queryById(id);
    }


    public List<AppInfo> queryAll() {
        return appInfoDao.queryAll();
    }


    public AppInfo queryByAppName(String appName) {
        return appInfoDao.queryByAppName(appName);
    }


    public void deleteByAppName(String appName) {
        if(appInfoDao.deleteByAppName(appName) !=1) {
            throw new MarsException(RespCode.DELETE_ERROR);
        }
    }
}
