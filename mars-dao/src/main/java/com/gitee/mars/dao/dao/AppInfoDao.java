package com.gitee.mars.dao.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gitee.mars.common.enums.RespCode;
import com.gitee.mars.common.exception.MarsException;
import com.gitee.mars.dao.entity.AppInfo;
import com.gitee.mars.dao.mapper.AppInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
@Slf4j
public class AppInfoDao  {
    @Autowired
    private AppInfoMapper appInfoDao;


    public Integer add(AppInfo appInfo) {

        if(appInfoDao.selectCount(new QueryWrapper<AppInfo>().eq("app_name",appInfo.getAppName())) > 0){
            throw new MarsException(RespCode.EXIST_ERROR,"应用");
        }
        appInfo.setCreateDate(new Date());
        return appInfoDao.insert(appInfo);
    }


    public Integer update(AppInfo appInfo) {

        appInfo.setUpdateDate(new Date());
        return appInfoDao.update(appInfo,new QueryWrapper<AppInfo>().eq("app_name", appInfo.getAppName()));
    }


    public Integer deleteById(Long id) {
        return appInfoDao.deleteById(id);
    }


    public AppInfo queryById(Long id) {
        return appInfoDao.selectById(id);
    }


    public List<AppInfo> queryAll() {
        return appInfoDao.selectList(null);
    }


    public AppInfo queryByAppName(String appName) {
        return appInfoDao.selectOne(new QueryWrapper<AppInfo>().eq("app_name", appName));
    }


    public int deleteByAppName(String appName) {
        return appInfoDao.delete(new QueryWrapper<AppInfo>().eq("app_name", appName));
    }

    public AppInfo selectOne(QueryWrapper<AppInfo> queryWrapper) {
        return appInfoDao.selectOne(queryWrapper);
    }
}
